(ns dinsro.actions.ln.remote-nodes
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.queries.ln.remote-nodes :as q.ln.remote-nodes]
   [lambdaisland.glogc :as log]))

(defn get-node-info
  [node pubkey]
  (with-open [client (a.ln.nodes/get-client node)]
    (c.lnd/get-node-info client pubkey)))

(defn register-node!
  [network-id pubkey]
  (log/finer :register-node!/starting {:pubkey pubkey})
  (if-let [node-id (q.ln.remote-nodes/find-by-network-and-pubkey network-id pubkey)]
    (do
      (log/info :register-node!/found {:pubkey pubkey :node-id node-id})
      node-id)
    (do
      (log/info :register-node!/not-found {:pubkey pubkey})
      (q.ln.remote-nodes/create-record
       {::m.ln.remote-nodes/pubkey  pubkey
        ::m.ln.remote-nodes/network network-id}))))

(defn fetch!
  [id]
  (log/info :fetch!/starting {:id id})
  nil)
