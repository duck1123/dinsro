^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.remote-nodes-notebook
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.remote-nodes :as q.ln.remote-nodes]
   [dinsro.queries.users :as q.users]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Remote Node Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

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

  (a.ln.remote-nodes/register-node!
   network-id
   (::m.ln.info/identity-pubkey node-alice))

  (<!! (a.ln.remote-nodes/get-node-info node "020e78000d4d907877ab352cd53c0dd382071c224b500c1fa05fb6f7902f5fa544"))
  (<!! (a.ln.remote-nodes/get-node-info node "02e21b44ba07591e43aa59a29f8631edb299d306d232a51a38f28d3892751dc13d"))

  nil)
