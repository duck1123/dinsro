(ns dinsro.actions.core.tx
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn => ?]]
   [dinsro.actions.core.node-base :as a.c.node-base]
   [dinsro.client.bitcoin-s :as c.bitcoin-s]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.tx :as q.c.tx]
   [dinsro.queries.core.tx-in :as q.c.tx-in]
   [dinsro.queries.core.tx-out :as q.c.tx-out]
   [lambdaisland.glogc :as log]))

(>defn fetch-tx
  [node tx-id]
  [::m.c.nodes/item ::m.c.tx/tx-id => any?]
  (let [node-id (::m.c.nodes/id node)]
    (log/info :fetch-tx/starting {:node-id node-id :tx-id tx-id})
    (let [client (a.c.node-base/get-client node)
          result (c.bitcoin-s/get-raw-transaction client tx-id)]
      (log/info :fetch-tx/finished {:result result})
      result)))

(>defn register-tx
  [core-node-id block-id tx-id]
  [::m.c.nodes/id ::m.c.blocks/id ::m.c.tx/tx-id => ::m.c.tx/id]
  (log/info :register-tx/starting {:block-id   block-id
                                   :core-node-id core-node-id
                                   :tx-id        tx-id})
  (if-let [id (q.c.tx/fetch-by-txid tx-id)]
    (do
      (log/info :register-tx/found {:id id})
      id)
    (do
      (log/info :register-tx/not-found {})
      (let [params   {::m.c.tx/block    block-id
                      ::m.c.tx/tx-id    tx-id
                      ::m.c.tx/fetched? false}]
        (q.c.tx/create-record params)))))

(defn update-tx-in
  [tx-id params]
  (log/info :update-tx-in/starting {:tx-id tx-id :params params})
  (let [params (assoc params ::m.c.tx-in/transaction tx-id)
        params (m.c.tx-in/prepare-params params)]
    (q.c.tx-in/create-record params)))

(>defn update-tx-out
  [tx-id old-output params]
  [::m.c.tx/id ::m.c.tx-out/item any? => ::m.c.tx-out/id]
  (log/info :update-tx-out/starting {:tx-id tx-id :out-output old-output :params params})
  (let [script-pub-key (:dinsro.client.converters.rpc-transaction-output/script-pub-key params)
        addresses (:dinsro.client.converters.rpc-script-pub-key/addresses script-pub-key)]
    (log/info :update-tx-out/found-addresses {:addresses addresses}))
  (let [tx-out-id (::m.c.tx-out/id old-output)
        params    (assoc params ::m.c.tx-out/id tx-out-id)
        params    (assoc params ::m.c.tx-out/transaction tx-id)
        params    (m.c.tx-out/prepare-params params)]
    (q.c.tx-out/update! tx-out-id params)
    tx-out-id))

(>defn create-tx-out
  [tx-id params]
  [::m.c.tx/id any? => ::m.c.tx-out/id]
  (log/info :create-tx-out/starting {})
  (let [params (assoc params ::m.c.tx-out/transaction tx-id)
        params (m.c.tx-out/prepare-params params)]
    (q.c.tx-out/create-record params)))

(>defn update-tx-out!
  [tx-id output]
  [::m.c.tx/id any? => ::m.c.tx-out/id]
  (log/info :update-tx-out!/starting {:tx-id tx-id :output output})
  (if-let [n (:dinsro.client.converters.rpc-transaction-output/n output)]
    (do
      (log/info :update-tx/handling-output {:output output :n n})
      (if-let [old-output-id (q.c.tx-out/find-by-tx-and-index tx-id n)]
        (do
          (log/info :update-tx/old-output-found {:old-output-id old-output-id})
          (if-let [old-output (q.c.tx-out/read-record old-output-id)]
            (update-tx-out tx-id old-output output)
            (throw (RuntimeException. "Failed to find old output id"))))
        (do
          (log/info :update-tx/old-output-not-found {})
          (create-tx-out tx-id output))))
    (throw (RuntimeException. "failed to find n"))))

(>defn update-tx
  [node-id block-id tx-id]
  [::m.c.nodes/id ::m.c.tx/block ::m.c.tx/tx-id => (? ::m.c.tx/id)]
  (log/info :update-tx/starting {:node-id node-id :tx-id tx-id})
  (if-let [node (q.c.nodes/read-record node-id)]
    (if-let [raw-tx (fetch-tx node tx-id)]
      (let [raw-tx         (assoc raw-tx ::m.c.tx/fetched? true)
            raw-tx         (assoc raw-tx  ::m.c.tx/block block-id)
            {:dinsro.client.converters.get-raw-transaction-result/keys
             [vin vout]}   raw-tx
            tx-params      (m.c.tx/prepare-params raw-tx)
            transaction-id (::m.c.tx/tx-id tx-params)]
        (if-let [existing-tx-id (q.c.tx/fetch-by-txid transaction-id)]
          (if-let [existing-tx (q.c.tx/read-record existing-tx-id)]
            (let [{block-id ::m.c.tx/block} existing-tx
                  tx-params                 (assoc tx-params ::m.c.tx/fetched? true)
                  tx-params                 (assoc tx-params ::m.c.tx/block block-id)]
              (q.c.tx/update-tx existing-tx-id tx-params)
              (doseq [output vout]
                (update-tx-out! existing-tx-id output))
              (doseq [input vin]
                (update-tx-in existing-tx-id input))
              existing-tx-id)
            (throw (RuntimeException. "failed to find tx")))
          (q.c.tx/create-record tx-params)))
      (throw (RuntimeException. "failed to find tx")))
    (throw (RuntimeException. "failed to find node"))))

(>def ::fetch-result (s/keys))

(>defn do-fetch!
  "Fetch tx info from node. Mutation handler"
  [{::m.c.tx/keys [id]}]
  [::m.c.tx/id-obj => ::fetch-result]
  (if-let [tx (q.c.tx/read-record id)]
    (if-let [block-id (::m.c.tx/block tx)]
      (if-let [block (q.c.blocks/read-record block-id)]
        (if-let [network-id (::m.c.blocks/network block)]
          (if-let [node-id (first (q.c.nodes/find-by-network network-id))]
            (let [{::m.c.tx/keys [tx-id]} tx

                  returned-id (update-tx node-id block-id tx-id)]
              {:status       :passed
               ::m.c.tx/item (q.c.tx/read-record returned-id)
               :id           returned-id})
            (do
              (log/warn :fetch!/no-node-id {:network-id network-id})
              {:status :failed}))
          (do
            (log/warn :fetch!/no-network-id {:block block})
            {:status :failed}))
        (do
          (log/info :fetch!/block-not-found {:block-id block-id})
          {:status :failed}))
      (do
        (log/warn :fetch!/no-block-id {:id id :tx tx})
        {:status :failed}))
    (do
      (log/warn :fetch!/tx-not-read {:id id})
      {:status :failed})))

(defn search!
  [props]
  (log/info :search!/searching {:props props})
  (let [{tx-id ::m.c.tx/tx-id
         node-id ::m.c.tx/node} props]
    (log/info :search!/started {:tx-id tx-id
                                :node-id node-id})
    (if-let [txid (q.c.tx/fetch-by-txid tx-id)]
      (do
        (log/info :search!/found {:tx-id tx-id :txid txid})
        (q.c.tx/read-record txid))
      (do
        (log/info :search!/not-cached {:tx-id tx-id})
        nil))))
