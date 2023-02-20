^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.actions.ln.transactions-notebook
  (:refer-clojure :exclude [next])
  (:require
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.transactions :as q.c.transactions]
   [dinsro.queries.core.tx-in :as q.c.tx-in]
   [dinsro.queries.core.tx-out :as q.c.tx-out]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Transaction Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(comment
  (q.c.blocks/index-ids)
  (q.c.transactions/index-ids)

  (map q.c.blocks/delete (q.c.blocks/index-ids))
  (map q.c.transactions/delete (q.c.transactions/index-ids))
  (map q.c.tx-out/delete! (q.c.tx-out/index-ids))
  (map q.c.tx-in/delete! (q.c.tx-in/index-ids))

  (def node-id (::m.ln.nodes/id n.lnd/node-alice))
  node-id
  (q.c.nodes/find-by-ln-node node-id)

  nil)
