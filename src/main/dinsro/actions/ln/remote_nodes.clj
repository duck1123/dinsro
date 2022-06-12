(ns dinsro.actions.ln.remote-nodes
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.remote-nodes :as m.ln.remote-nodes]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.remote-nodes :as q.ln.remote-nodes]
   [dinsro.queries.users :as q.users]
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

(comment
  (def node-alice (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "alice") "lnd-alice")))
  (def node-bob (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))
  (def node node-alice)
  node-alice
  node-bob
  node

  (q.ln.remote-nodes/index-records)

  (def network-id (q.c.networks/find-by-chain-and-network "bitcoin" "regtest"))
  network-id

  (register-node!
   network-id
   (::m.ln.info/identity-pubkey node-alice))

  (<!! (get-node-info node "020e78000d4d907877ab352cd53c0dd382071c224b500c1fa05fb6f7902f5fa544"))
  (<!! (get-node-info node "02e21b44ba07591e43aa59a29f8631edb299d306d232a51a38f28d3892751dc13d"))

  nil)
