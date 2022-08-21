^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.remote-nodes-notebook
  (:require
   [dinsro.actions.ln.remote-nodes :as a.ln.remote-nodes]
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.ln.remote-nodes :as q.ln.remote-nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Remote Node Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment
  (q.ln.remote-nodes/index-records)

  (def network-id (q.c.networks/find-by-chain-and-network "bitcoin" "regtest"))
  network-id

  (a.ln.remote-nodes/register-node!
   network-id
   (::m.ln.info/identity-pubkey n.lnd/node-alice)
   nil)

  nil)
