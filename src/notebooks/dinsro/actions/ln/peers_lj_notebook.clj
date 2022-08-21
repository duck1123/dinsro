^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.peers-lj-notebook
  (:refer-clojure :exclude [next])
  (:require
   [dinsro.actions.ln.peers-lj :as a.ln.peers-lj]
   [dinsro.actions.ln.peers-notebook :as a.ln.peers-notebook]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Peer Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

;; ## create-peer!

(comment

  (a.ln.peers-lj/create-peer!
   n.lnd/node-alice
   a.ln.peers-notebook/address
   (::m.ln.info/identity-pubkey n.lnd/node-bob))

  (a.ln.peers-lj/create-peer!
   n.lnd/node-bob
   (str (::m.ln.nodes/host n.lnd/node-alice) ":9735")
   (::m.ln.info/identity-pubkey n.lnd/node-alice))

  nil)

(comment)
