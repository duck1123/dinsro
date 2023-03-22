^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.actions.ln.remote-nodes-notebook
  (:require
   [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Remote Node Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(comment
  (def network-id (q.c.networks/find-by-chain-and-network "bitcoin" "regtest"))
  network-id

  (a.ln.remote-nodes/register-node!
   network-id
   (::m.ln.info/identity-pubkey n.lnd/node-alice)
   nil)

  nil)
