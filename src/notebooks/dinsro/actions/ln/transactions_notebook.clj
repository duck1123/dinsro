^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.transactions-notebook
  (:refer-clojure :exclude [next])
  (:require
   [dinsro.lnd-notebook :as n.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.tx :as q.c.tx]
   [dinsro.queries.core.tx-in :as q.c.tx-in]
   [dinsro.queries.core.tx-out :as q.c.tx-out]
   [dinsro.queries.ln.transactions :as q.ln.tx]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Transaction Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment
  (q.c.blocks/index-ids)
  (q.c.tx/index-ids)
  (q.ln.tx/index-records)
  (q.ln.tx/index-ids)

  (map q.c.blocks/delete (q.c.blocks/index-ids))
  (map q.c.tx/delete (q.c.tx/index-ids))
  (map q.c.tx-out/delete! (q.c.tx-out/index-ids))
  (map q.c.tx-in/delete! (q.c.tx-in/index-ids))
  (map q.ln.tx/delete! (q.ln.tx/index-ids))

  (def node-id (::m.ln.nodes/id n.lnd/node-alice))
  node-id
  (q.c.nodes/find-by-ln-node node-id)

  nil)
