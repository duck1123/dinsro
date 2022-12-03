(ns dinsro.actions.ln.transactions
  (:refer-clojure :exclude [next])
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [lambdaisland.glogc :as log]))

(defn not-implemented [] (throw (RuntimeException. "not-implemented")))

(>defn fetch-transactions
  [node]
  [::m.ln.nodes/item => any?]
  (comment node)
  (not-implemented))

(>defn handle-get-transactions-response
  [node-id transaction]
  [::m.ln.nodes/id any? => ::m.c.tx/id]
  (comment node-id transaction)
  (not-implemented))

(>defn update-transaction!
  [node data]
  [::m.ln.nodes/item any? => (? ::m.c.tx/id)]
  (log/info :update-transaction!/starting {})
  (comment node data)
  (not-implemented))

(>defn update-transactions!
  [node]
  [::m.ln.nodes/item => any?]
  (comment node)
  (not-implemented))

(>defn fetch-transactions!
  [node-id]
  [::m.ln.nodes/id => any?]
  (log/info :fetch-transactions!/starting {:node-id node-id})
  (if-let [node (q.ln.nodes/read-record node-id)]
    (update-transactions! node)
    (do
      (log/error :fetch-transactions!/no-node {})
      nil)))
