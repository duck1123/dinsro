^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.peers-notebook
  (:require
   [dinsro.actions.core.peers :as a.c.peers]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.peers :as q.c.peers]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Peer Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment

  (def node1 (first (q.c.nodes/index-ids)))
  (def node2 (second (q.c.nodes/index-ids)))
  (map q.c.peers/find-by-core-node (q.c.nodes/index-ids))
  (q.c.nodes/read-record node1)

  (a.c.peers/get-peer-info (q.c.nodes/read-record node1))

  (::m.c.nodes/host (q.c.nodes/read-record node2))

  (a.c.peers/add-peer!
   (q.c.nodes/read-record node1)
   (::m.c.nodes/host (q.c.nodes/read-record node2)))

  (def peer (first (q.c.peers/index-records)))
  (a.c.peers/delete! peer)
  (tap> peer)

  (tap> (q.c.peers/index-records))

  node2

  nil)
