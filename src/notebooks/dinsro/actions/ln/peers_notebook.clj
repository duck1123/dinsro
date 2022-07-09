^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.peers-notebook
  (:refer-clojure :exclude [next])
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.actions.ln.peers :as a.ln.peers]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.queries.users :as q.users]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Peer Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment
  (q.ln.peers/index-ids)
  (q.ln.peers/index-records)

  (map ::m.ln.info/identity-pubkey (q.ln.nodes/index-records))

  (def node-alice (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "alice") "lnd-alice")))
  (def node-bob (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))

  (tap> node-alice)
  (tap> node-bob)

  (def address
    (str
     (::m.ln.info/identity-pubkey node-bob)
     "@"
     (::m.ln.nodes/host node-bob)
     ":"
     (::m.ln.nodes/port node-bob)))

  address

  (a.ln.nodes/download-cert! node-alice)
  (a.ln.nodes/download-macaroon! node-alice)

  (a.ln.nodes/download-cert! node-bob)
  (a.ln.nodes/download-macaroon! node-bob)

  (a.ln.peers/create-peer!
   node-alice
   (str
    (::m.ln.nodes/host node-bob)
    ":"
    (::m.ln.nodes/port node-bob))
   (::m.ln.info/identity-pubkey node-bob))

  (a.ln.peers/create-peer!
   node-bob
   (str
    (::m.ln.nodes/host node-alice)
    ":9735")

   (::m.ln.info/identity-pubkey node-alice))

  (def peer (first (q.ln.peers/index-records)))
  (tap> peer)
  (def node-id (::m.ln.peers/node peer))
  node-id

  (def node (q.ln.nodes/read-record node-id))
  node
  (def pubkey (::m.ln.info/identity-pubkey node))
  pubkey

  (first (q.ln.nodes/index-records))

  (q.ln.peers/find-peer node-id pubkey)

  (map q.ln.peers/delete (q.ln.peers/index-ids))
  (first (q.ln.peers/index-records))

  nil)
