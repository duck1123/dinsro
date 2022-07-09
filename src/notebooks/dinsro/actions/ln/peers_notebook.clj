^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.peers-notebook
  (:refer-clojure :exclude [next])
  (:require
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.actions.ln.nodes-notebook :as n.a.ln.nodes]
   [dinsro.actions.ln.peers :as a.ln.peers]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.peers :as m.ln.peers]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.peers :as q.ln.peers]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Peer Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(def address
  (str
   (::m.ln.info/identity-pubkey n.a.ln.nodes/node-bob)
   "@"
   (::m.ln.nodes/host n.a.ln.nodes/node-bob)
   ":"
   (::m.ln.nodes/port n.a.ln.nodes/node-bob)))

;; ## create-peer!

(comment

  (a.ln.peers/create-peer!
   n.a.ln.nodes/node-alice
   address
   (::m.ln.info/identity-pubkey n.a.ln.nodes/node-bob))

  (a.ln.peers/create-peer!
   n.a.ln.nodes/node-bob
   (str
    (::m.ln.nodes/host n.a.ln.nodes/node-alice)
    ":9735")
   (::m.ln.info/identity-pubkey n.a.ln.nodes/node-alice))

  nil)

(comment
  (q.ln.peers/index-ids)
  (q.ln.peers/index-records)

  (map ::m.ln.info/identity-pubkey (q.ln.nodes/index-records))

  (a.ln.nodes/download-cert! n.a.ln.nodes/node-alice)
  (a.ln.nodes/download-macaroon! n.a.ln.nodes/node-alice)

  (a.ln.nodes/download-cert! n.a.ln.nodes/node-bob)
  (a.ln.nodes/download-macaroon! n.a.ln.nodes/node-bob)

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
