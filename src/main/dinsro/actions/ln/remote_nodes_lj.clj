(ns dinsro.actions.ln.remote-nodes-lj
  (:require
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.client.lnd :as c.lnd]))

(defn get-node-info
  [node pubkey]
  (with-open [client (a.ln.nodes-lj/get-client node)]
    (c.lnd/get-node-info client pubkey)))
