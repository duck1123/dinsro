^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.ln.peers-notebook
  (:refer-clojure :exclude [next])
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Peer Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def address
  (str
   (::m.ln.info/identity-pubkey n.lnd/node-bob)
   "@"
   (::m.ln.nodes/host n.lnd/node-bob)
   ":"
   (::m.ln.nodes/port n.lnd/node-bob)))

(comment
  (q.ln.peers/index-ids)
  (q.ln.peers/index-records)

  (a.ln.nodes/download-cert! n.lnd/node-alice)
  (a.ln.nodes/download-macaroon! n.lnd/node-alice)

  (a.ln.nodes/download-cert! n.lnd/node-bob)
  (a.ln.nodes/download-macaroon! n.lnd/node-bob)

  (def peer (first (q.ln.peers/index-records)))
  (tap> peer)
  (def node-id (::m.ln.peers/node peer))
  node-id

  (def node (q.ln.nodes/read-record node-id))
  node
  (def pubkey (::m.ln.info/identity-pubkey node))
  pubkey

  (q.ln.peers/find-peer node-id pubkey)

  (map q.ln.peers/delete (q.ln.peers/index-ids))
  (first (q.ln.peers/index-records))

  nil)
