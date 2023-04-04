(ns dinsro.actions.ln.transactions
  (:refer-clojure :exclude [next])
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.model.core.transactions :as m.c.transactions]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [lambdaisland.glogc :as log]))

(defn not-implemented [] (throw (ex-info "not-implemented" {})))

(>defn handle-get-transactions-response
  [node-id transaction]
  [::m.ln.nodes/id any? => ::m.c.transactions/id]
  (comment node-id transaction)
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
