(ns dinsro.actions.ln-remote-nodes
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [dinsro.actions.ln-nodes :as a.ln-nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.queries.ln-nodes :as q.ln-nodes]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(defn get-node-info
  [node pubkey]
  (with-open [client (a.ln-nodes/get-client node)]
    (c.lnd/get-node-info client pubkey)))

(comment
  (def node-alice (q.ln-nodes/read-record (q.ln-nodes/find-id-by-user-and-name (q.users/find-eid-by-name "alice") "lnd-alice")))
  (def node-bob (q.ln-nodes/read-record (q.ln-nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))
  (def node node-alice)
  node-alice
  node-bob
  node

  (<!! (get-node-info node "020e78000d4d907877ab352cd53c0dd382071c224b500c1fa05fb6f7902f5fa544"))
  (<!! (get-node-info node "02e21b44ba07591e43aa59a29f8631edb299d306d232a51a38f28d3892751dc13d"))

  nil)
