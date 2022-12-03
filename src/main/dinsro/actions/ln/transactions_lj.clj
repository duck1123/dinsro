(ns dinsro.actions.ln.transactions-lj
  (:refer-clojure :exclude [next])
  (:require
   [clojure.core.async :as async :refer [<!]]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.actions.ln.transactions :as a.ln.transactions]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

(defn not-implemented [] (throw (RuntimeException. "not-implemented")))

(>defn fetch-transactions
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (with-open [client (a.ln.nodes-lj/get-client node)]
    (c.lnd/get-transactions client)))

(>defn get-transactions
  [node]
  [::m.ln.nodes/item => ds/channel?]
  (with-open [client (a.ln.nodes-lj/get-client node)]
    (c.lnd/get-transactions client)))

(>defn update-transactions
  [{::m.ln.nodes/keys [id] :as node}]
  [::m.ln.nodes/item => ds/channel?]
  (log/info :update-transactions/starting {})
  (let [ch (get-transactions node)]
    (async/go
      (let [response               (<! ch)
            {:keys [transactions]} (c.lnd/parse response)]
        (doseq [transaction (take 3 transactions)]
          (a.ln.transactions/handle-get-transactions-response id transaction))))))

(>defn save-transactions!
  [ln-node-id tx-id params]
  [::m.ln.nodes/id ::m.c.tx/id any? => any?]
  (comment ln-node-id tx-id params)
  (not-implemented))

(>defn update-transaction!
  [node data]
  [::m.ln.nodes/item any? => (? ::m.c.tx/id)]
  (log/info :update-transaction!/starting {})
  (comment node data)
  (not-implemented))

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
              (let [params (assoc params ::m.c.tx/node node-id)
                    params (m.c.tx/prepare-params params)]
                (update-transaction! node params)))))
        ch)
      (do
        (log/error :update-transactions!/channel-error {})
        nil))))

(>defn fetch-transactions!
  [node-id]
  [::m.ln.nodes/id => (? ds/channel?)]
  (log/info :fetch-transactions!/starting {:node-id node-id})
  (if-let [node (q.ln.nodes/read-record node-id)]
    (update-transactions! node)
    (do
      (log/error :fetch-transactions!/no-node {})
      nil)))
