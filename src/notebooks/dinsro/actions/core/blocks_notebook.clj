^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.blocks-notebook
  (:require
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Block Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment
  (def node-alice (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-alice")))
  (def node-bob (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-bob")))
  (def node node-alice)

  (tap> (q.c.blocks/index-records))

  (q.c.blocks/index-records)
  (map
   q.c.blocks/read-record
   (q.c.blocks/find-by-node (::m.c.nodes/id node-alice)))
  (q.c.blocks/find-by-node (::m.c.nodes/id node-bob))

  (q.c.blocks/fetch-by-node-and-height (::m.c.nodes/id node-alice) 97)

  nil)
