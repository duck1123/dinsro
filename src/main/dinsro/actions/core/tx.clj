(ns dinsro.actions.core.tx
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.core.blocks :as a.c.blocks]
   [dinsro.client.bitcoin :as c.bitcoin]
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
  (log/info :tx/fetch {:node node :tx-id tx-id})
  (let [client (m.c.nodes/get-client node)
        result (c.bitcoin/get-raw-transaction client tx-id)]
    (log/info :tx/fetched {:result result})
    result))

(>defn register-tx
  [core-node-id block-hash block-height tx-id]
  [::m.c.nodes/id ::m.c.blocks/hash ::m.c.blocks/height ::m.c.tx/tx-id => ::m.c.tx/id]
  (log/info :tx/register {:block-hash   block-hash
                          :block-height block-height
                          :core-node-id core-node-id
                          :tx-id        tx-id})
  (if-let [id (q.c.tx/fetch-by-txid tx-id)]
    (do
      (log/info :tx/found {:id id})
      id)
    (do
      (log/info :tx/not-found {})
      (let [block-id (a.c.blocks/register-block core-node-id block-hash block-height)
            params   {::m.c.tx/block    block-id
                      ::m.c.tx/tx-id    tx-id
                      ::m.c.tx/fetched? false}]
        (q.c.tx/create-record params)))))

(defn update-tx-in
  [tx-id params]
  (let [params (assoc params ::m.c.tx-in/transaction tx-id)
        params (m.c.tx-in/prepare-params params)]
    (q.c.tx-in/create-record params)))

(>defn update-tx-out
  [tx-id old-output params]
  [::m.c.tx/id ::m.c.tx-out/item any? => ::m.c.tx-out/id]
  (log/info :tx-out/update {:tx-id tx-id :out-output old-output :params params})
  (let [tx-out-id (::m.c.tx-out/id old-output)
        params    (assoc params ::m.c.tx-out/id tx-out-id)
        params    (assoc params ::m.c.tx-out/transaction tx-id)
        params    (m.c.tx-out/prepare-params params)]
    (q.c.tx-out/update! params)
    tx-out-id))

(>defn create-tx-out
  [tx-id params]
  [::m.c.tx/id any? => ::m.c.tx-out/id]
  (log/info :tx-out/create {})
  (let [params (assoc params ::m.c.tx-out/transaction tx-id)
        params (m.c.tx-out/prepare-params params)]
    (q.c.tx-out/create-record params)))

(>defn update-tx
  [node-id tx-id]
  [::m.c.nodes/id ::m.c.tx/tx-id => (? ::m.c.tx/id)]
  (log/info :tx/update {:node-id node-id :tx-id tx-id})
  (if-let [node (q.c.nodes/read-record node-id)]
    (if-let [raw-tx (fetch-tx node tx-id)]
      (let [tx-params (assoc (m.c.tx/prepare-params raw-tx) ::m.c.tx/node node-id)]
        (if-let [existing-tx-id (q.c.tx/fetch-by-txid (::m.c.tx/tx-id tx-params))]
          (if-let [existing-tx (q.c.tx/read-record existing-tx-id)]
            (let [{::m.c.tx/keys [block]} existing-tx
                  tx-params                  (assoc tx-params ::m.c.tx/fetched? true)
                  tx-params                  (assoc tx-params ::m.c.tx/block block)]
              (q.c.tx/update-tx existing-tx-id tx-params)
              (let [{:keys [vin vout]} raw-tx]
                (doseq [output vout]
                  (let [n (:n output)]
                    (if-let [old-output-id (q.c.tx-out/find-by-tx-and-index existing-tx-id n)]
                      (if-let [old-output (q.c.tx-out/read-record old-output-id)]
                        (update-tx-out existing-tx-id old-output output)
                        (throw (RuntimeException. "Failed to find old output id")))
                      (create-tx-out existing-tx-id output))))
                (doseq [input vin]
                  (update-tx-in existing-tx-id input)))
              existing-tx-id)
            (throw (RuntimeException. "failed to find tx")))
          (q.c.tx/create-record tx-params)))
      (throw (RuntimeException. "failed to find tx")))
    (throw (RuntimeException. "failed to find node"))))

(>defn fetch!
  "Fetch tx info from node"
  [{::m.c.tx/keys [id]}]
  [::m.c.tx/id-obj => (s/keys)]
  (let [{::m.c.tx/keys [tx-id]
         block-id         ::m.c.tx/block} (q.c.tx/read-record id)]
    (if-let [block (q.c.blocks/read-record block-id)]
      (let [{::m.c.blocks/keys [node]} block
            returned-id                  (update-tx node tx-id)]
        {:status          :passed
         ::m.c.tx/item (q.c.tx/read-record returned-id)
         :id              returned-id})
      (do
        (log/info :block/failed-to-find {})
        {:status :failed}))))

(defn search!
  [props]
  (log/info :tx/searching {:props props})
  (let [{tx-id ::m.c.tx/tx-id
         node-id ::m.c.tx/node} props]
    (log/info :search/started {:tx-id tx-id
                               :node-id node-id})
    (if-let [txid (q.c.tx/fetch-by-txid tx-id)]
      (do
        (log/info :search/found {:tx-id tx-id :txid txid})
        (q.c.tx/read-record txid))
      (do
        (log/info :fetch/not-cached {:tx-id tx-id})
        nil))))

(comment
  (def node-alice (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-alice")))
  (def node-bob (q.c.nodes/read-record (q.c.nodes/find-id-by-name "bitcoin-bob")))

  (tap> (q.c.nodes/index-records))

  (update-tx (first (q.c.nodes/index-ids))
             "8d3b5c3f7e726b57cdd293885f74c28773ee9682548756c7f393e76a2b935a20")

  (def node node-alice)
  (def node-id (::m.c.nodes/id node))
  (def tx-id "0ee607c65f65ccb74f79f4cd936dedb7779199aabaca417ac3ca63a7a23daed4")

  (q.c.nodes/index-ids)

  (def id (first (q.c.tx/index-ids)))
  (def tx (q.c.tx/read-record id))
  (def block-id (::m.c.tx/block tx))
  (q.c.blocks/read-record block-id)
  (q.c.nodes/find-by-tx id)

  (q.c.tx/index-ids)
  (def tx-id2 (::m.c.tx/tx-id (first (q.c.tx/index-records))))
  tx-id2

  (search! {::m.c.tx/tx-id tx-id2})
  (search! {::m.c.tx/tx-id "foo"})
  (tap> (search! {::m.c.tx/tx-id tx-id2}))

  (q.c.tx-in/index-records)
  (q.c.tx-out/index-ids)

  (map q.c.tx-out/delete! (q.c.tx-out/index-ids))
  (map q.c.tx-in/delete! (q.c.tx-in/index-ids))
  (map q.c.tx/delete (q.c.tx/index-ids))
  (map q.c.blocks/delete (q.c.blocks/index-ids))

  (q.c.blocks/index-ids)

  (update-tx node-id tx-id)

  nil)
