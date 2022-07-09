(ns dinsro.actions.ln.transactions
  (:refer-clojure :exclude [next])
  (:require
   [clojure.core.async :as async :refer [<!]]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.core.tx :as a.c.tx]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.transactions :as q.ln.tx]
   [dinsro.specs :as ds]
   [taoensso.timbre :as log]))

(>defn fetch-transactions
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (with-open [client (a.ln.nodes/get-client node)]
    (c.lnd/get-transactions client)))

(>defn save-transactions!
  [ln-node-id tx-id params]
  [::m.ln.nodes/id
   ::m.c.tx/id any? => any?]
  (let [params (assoc params ::m.ln.tx/core-tx tx-id)
        params (assoc params ::m.ln.tx/node ln-node-id)
        params (m.ln.tx/prepare-params params)]
    (q.ln.tx/create-record params)))

(>defn update-transaction!
  [node data]
  [::m.ln.nodes/item ::m.ln.tx/raw-params => (? ::m.ln.tx/id)]
  (log/info "updating transaction")
  (if-let [ln-node-id (::m.ln.nodes/id node)]
    (let [{::m.ln.tx/keys [block-hash block-height tx-hash]} data]
      (if-let [tx-id (q.ln.tx/find-id-by-node-and-tx-hash ln-node-id tx-hash)]
        (do
          (log/infof "has tx: %s" tx-id)
          tx-id)
        (if-let [core-node-id (q.c.nodes/find-by-ln-node ln-node-id)]
          (do (log/error "no tx")
              (let [core-tx-id (a.c.tx/register-tx core-node-id block-hash block-height tx-hash)]
                (save-transactions! ln-node-id core-tx-id data)))
          (throw (RuntimeException. (str "failed to find core node: " node))))))
    (throw (RuntimeException. "failed to find node id"))))

(>defn handle-get-transactions-response
  [node-id transaction]
  [::m.ln.nodes/id any? => ::m.ln.tx/id]
  (let [params (m.ln.tx/prepare-params transaction)
        params (assoc params ::m.ln.tx/node node-id)]
    (q.ln.tx/create-record params)))

(>defn update-transactions!
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (let [node-id (::m.ln.nodes/id node)]
    (if-let [ch (fetch-transactions node)]
      (do
        (async/go
          (let [data                   (async/<! ch)
                {:keys [transactions]} data]
            (doseq [params (take 3 transactions)]
              (let [params (assoc params ::m.ln.tx/node node-id)
                    params (m.ln.tx/prepare-params params)]
                (update-transaction! node params)))))
        ch)
      (do
        (log/error "channel error")
        nil))))

(>defn fetch-transactions!
  [node-id]
  [::m.ln.nodes/id => (? ds/channel?)]
  (log/infof "Fetching Transactions - %s" node-id)
  (if-let [node (q.ln.nodes/read-record node-id)]
    (update-transactions! node)
    (do
      (log/error "No Node")
      nil)))

(>defn get-transactions
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (with-open [client (a.ln.nodes/get-client node)]
    (c.lnd/get-transactions client)))

(>defn update-transactions
  [{::m.ln.nodes/keys [id] :as node}]
  [::m.ln.nodes/item => ds/channel?]
  (log/info "updating transactions")
  (let [ch (get-transactions node)]
    (async/go
      (let [response               (<! ch)
            {:keys [transactions]} (c.lnd/parse response)]
        (doseq [transaction (take 3 transactions)]
          (handle-get-transactions-response id transaction))))))
