^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.lnd-notebook
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.users :as q.users]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Notebook

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def user-alice (q.users/find-eid-by-name "alice"))
(def alice-id (q.users/find-eid-by-name "alice"))
(def user-bob (q.users/find-eid-by-name "bob"))
(def node-alice-id (q.ln.nodes/find-id-by-user-and-name alice-id "lnd-alice"))
(def node-alice (q.ln.nodes/read-record node-alice-id))
(def node-bob-id (q.ln.nodes/find-id-by-user-and-name user-bob "lnd-bob"))
(def node-bob (q.ln.nodes/read-record node-bob-id))
(def node node-alice)

;; (a.ln.nodes/get-cert-text node)
;; (a.ln.nodes/get-macaroon-hex node)

(def client (a.ln.nodes/get-client-s node))
