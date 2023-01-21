^{:nextjournal.clerk/visibility #{:code :hide}}
(ns dinsro.lnd-notebook
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.users :as q.users]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Notebook

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def user-alice (q.users/find-by-name "alice"))
(def alice-id (q.users/find-by-name "alice"))
(def user-bob (q.users/find-by-name "bob"))
(def node-alice-id (q.ln.nodes/find-by-user-and-name alice-id "lnd-alice"))
(def node-alice (q.ln.nodes/read-record node-alice-id))
(def node-bob-id (q.ln.nodes/find-by-user-and-name user-bob "lnd-bob"))
(def node-bob (q.ln.nodes/read-record node-bob-id))
(def node node-alice)
(def core-node-alice (q.c.nodes/read-record (q.c.nodes/find-by-ln-node (::m.ln.nodes/id node-alice))))
(def core-node-bob (q.c.nodes/read-record (q.c.nodes/find-by-ln-node (::m.ln.nodes/id node-bob))))

;; (a.ln.nodes/get-cert-text node)
;; (a.ln.nodes/get-macaroon-hex node)

(def client (a.ln.nodes/get-client node))
