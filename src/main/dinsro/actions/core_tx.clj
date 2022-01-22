(ns dinsro.actions.core-tx
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.core-block :as a.core-block]
   [dinsro.model.core-block :as m.core-block]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.model.core-tx-in :as m.core-tx-in]
   [dinsro.model.core-tx-out :as m.core-tx-out]
   [dinsro.queries.core-block :as q.core-block]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.core-tx :as q.core-tx]
   [dinsro.queries.core-tx-in :as q.core-tx-in]
   [dinsro.queries.core-tx-out :as q.core-tx-out]
   [farseer.client :as client]
   [taoensso.timbre :as log]))

(>defn fetch-tx
  [node tx-id]
  [::m.core-nodes/item ::m.core-tx/tx-id => any?]
  (let [client (m.core-nodes/get-client node)]
    (:result (client/call client :getrawtransaction [tx-id true]))))

(>defn register-tx
  [core-node-id block-hash block-height tx-id]
  [::m.core-nodes/id ::m.core-block/hash ::m.core-block/height ::m.core-tx/tx-id => ::m.core-tx/id]
  (log/info "registering tx")
  (if-let [id (q.core-tx/fetch-by-txid tx-id)]
    (do
      (log/info "found")
      id)
    (do
      (log/info "not found")
      (let [block-id (a.core-block/register-block core-node-id block-hash block-height)
            params   {::m.core-tx/block    block-id
                      ::m.core-tx/tx-id    tx-id
                      ::m.core-tx/fetched? false}]
        (q.core-tx/create-record params)))))

(defn update-tx-in
  [tx-id params]
  (let [params (assoc params ::m.core-tx-in/transaction tx-id)
        params (m.core-tx-in/prepare-params params)]
    (q.core-tx-in/create-record params)))

(>defn update-tx-out
  [tx-id old-output params]
  [::m.core-tx/id ::m.core-tx-out/item any? => ::m.core-tx-out/id]
  (log/info "updating tx out")
  (let [tx-out-id (::m.core-tx-out/id old-output)
        params    (assoc params ::m.core-tx-out/id tx-out-id)
        params    (assoc params ::m.core-tx-out/transaction tx-id)
        params    (m.core-tx-out/prepare-params params)]
    (q.core-tx-out/update! params)
    tx-out-id))

(>defn create-tx-out
  [tx-id params]
  [::m.core-tx/id any? => ::m.core-tx-out/id]
  (log/info "creating tx output")
  (let [params (assoc params ::m.core-tx-out/transaction tx-id)
        params (m.core-tx-out/prepare-params params)]
    (q.core-tx-out/create-record params)))

(>defn update-tx
  [node-id tx-id]
  [::m.core-nodes/id ::m.core-tx/tx-id => (? ::m.core-tx/id)]
  (log/info "Updating tx")
  (if-let [node (q.core-nodes/read-record node-id)]
    (if-let [raw-tx (fetch-tx node tx-id)]
      (let [tx-params (assoc (m.core-tx/prepare-params raw-tx) ::m.core-tx/node node-id)]
        (if-let [existing-tx-id (q.core-tx/fetch-by-txid (::m.core-tx/tx-id tx-params))]
          (if-let [existing-tx (q.core-tx/read-record existing-tx-id)]
            (let [{::m.core-tx/keys [block]} existing-tx
                  tx-params                  (assoc tx-params ::m.core-tx/fetched? true)
                  tx-params                  (assoc tx-params ::m.core-tx/block block)]
              (q.core-tx/update-tx existing-tx-id tx-params)
              (let [{:keys [vin vout]} raw-tx]
                (doseq [output vout]
                  (let [n (:n output)]
                    (if-let [old-output-id (q.core-tx-out/find-by-tx-and-index existing-tx-id n)]
                      (if-let [old-output (q.core-tx-out/read-record old-output-id)]
                        (update-tx-out existing-tx-id old-output output)
                        (throw (RuntimeException. "Failed to find old output id")))
                      (create-tx-out existing-tx-id output))))
                (doseq [input vin]
                  (update-tx-in existing-tx-id input)))
              existing-tx-id)
            (throw (RuntimeException. "failed to find tx")))
          (q.core-tx/create-record tx-params)))
      (throw (RuntimeException. "failed to find tx")))
    (throw (RuntimeException. "failed to find node"))))

(>defn fetch!
  "Fetch tx info from node"
  [{::m.core-tx/keys [id]}]
  [::m.core-tx/id-obj => (s/keys)]
  (let [{::m.core-tx/keys [tx-id]
         block-id         ::m.core-tx/block} (q.core-tx/read-record id)]
    (if-let [block (q.core-block/read-record block-id)]
      (let [{::m.core-block/keys [node]} block
            returned-id                  (update-tx node tx-id)]
        {:status :passed
         :id     returned-id})
      (do
        (log/info "failed to find block")
        {:status :failed}))))

(comment
  (def node-alice (q.core-nodes/read-record (q.core-nodes/find-id-by-name "bitcoin-alice")))
  (def node-bob (q.core-nodes/read-record (q.core-nodes/find-id-by-name "bitcoin-bob")))

  (def node node-alice)
  (def node-id (::m.core-nodes/id node))
  (def tx-id "0ee607c65f65ccb74f79f4cd936dedb7779199aabaca417ac3ca63a7a23daed4")

  (q.core-nodes/index-ids)

  (def id (first (q.core-tx/index-ids)))
  (def tx (q.core-tx/read-record id))
  (def block-id (::m.core-tx/block tx))
  (q.core-block/read-record block-id)
  (q.core-nodes/find-by-tx id)

  (q.core-tx/index-ids)

  (q.core-tx-in/index-records)
  (q.core-tx-out/index-ids)

  (map q.core-tx-out/delete! (q.core-tx-out/index-ids))
  (map q.core-tx-in/delete! (q.core-tx-in/index-ids))
  (map q.core-tx/delete (q.core-tx/index-ids))
  (map q.core-block/delete (q.core-block/index-ids))

  (q.core-block/index-ids)

  (update-tx node-id tx-id)

  nil)