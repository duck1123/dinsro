^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.blocks-notebook
  (:require
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.blocks :as q.c.blocks]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Block Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

^{::clerk/viewer clerk/code}
(def node-alice (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-alice")))
(def node-bob (q.c.nodes/read-record (q.c.nodes/find-by-name "bitcoin-bob")))
(def node node-alice)
(def alice-id (::m.c.nodes/id node-alice))

(def alice-block-ids (q.c.blocks/find-by-node alice-id))

(def all-ids (q.c.blocks/index-ids))

^{::clerk/viewer clerk/code}
(q.c.networks/read-record (q.c.networks/find-by-core-node alice-id))

;; ## nodes


^{::clerk/viewer clerk/table}
(map q.c.nodes/read-record (q.c.nodes/index-ids))

;; ## blocks

^{::clerk/viewer clerk/table}
(map q.c.blocks/read-record all-ids)

^{::clerk/viewer clerk/table}
(map q.c.blocks/read-record alice-block-ids)

(comment

  (tap> (q.c.blocks/index-records))

  (q.c.blocks/index-records)
  (q.c.blocks/find-by-node (::m.c.nodes/id node-bob))

  (q.c.blocks/fetch-by-node-and-height (::m.c.nodes/id node-alice) 97)

  nil)
