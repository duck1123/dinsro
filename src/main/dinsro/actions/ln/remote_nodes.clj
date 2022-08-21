(ns dinsro.actions.ln.remote-nodes
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.queries.ln.remote-nodes :as q.ln.remote-nodes]
   [lambdaisland.glogc :as log]))

(>defn register-node!
  [node-id pubkey host]
  [::m.ln.remote-nodes/node
   ::m.ln.remote-nodes/pubkey
   ::m.ln.remote-nodes/host => ::m.ln.remote-nodes/id]
  (log/finer :register-node!/starting {:node-id node-id :pubkey pubkey})
  (if-let [remote-node-id (q.ln.remote-nodes/find-by-node-and-pubkey node-id pubkey)]
    (do
      (log/info :register-node!/found {:pubkey pubkey :remote-node-id remote-node-id})
      remote-node-id)
    (do
      (log/info :register-node!/not-found {:pubkey pubkey})
      (q.ln.remote-nodes/create-record
       (merge
        {::m.ln.remote-nodes/pubkey pubkey
         ::m.ln.remote-nodes/node   node-id}
        (when host {::m.ln.remote-nodes/host host}))))))

(defn fetch!
  [id]
  (log/info :fetch!/starting {:id id})
  nil)
