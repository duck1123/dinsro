^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.actions.core.peers-notebook
  (:require
   [dinsro.actions.core.peers :as a.c.peers]
   [dinsro.model.core.nodes :as m.c.nodes]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.core.peers :as q.c.peers]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Peer Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(def node1-id (first (q.c.nodes/index-ids)))
(def node2-id (second (q.c.nodes/index-ids)))

(def node1 (q.c.nodes/read-record node1-id))
(def node2 (q.c.nodes/read-record node2-id))

(def remote-host (::m.c.nodes/host node2))
(def remote-host-url (str "http://" remote-host))

;; ## get-peer-info

(comment

  (a.c.peers/get-peer-info node1)

  nil)

;; ## has-peer?

(comment

  (a.c.peers/has-peer? node1 remote-host-url)

  nil)

;; ## fetch-peers!

(comment

  (a.c.peers/fetch-peers! node1)

  nil)

;; ## add-peer!

(comment

  (a.c.peers/add-peer! node1 remote-host-url)

  nil)

;; ## other

(comment

  (map q.c.peers/find-by-core-node (q.c.nodes/index-ids))
  (q.c.nodes/read-record node1)

  (::m.c.nodes/host (q.c.nodes/read-record node2))

  (def peer (q.c.peers/read-record (first (q.c.peers/index-ids))))
  (a.c.peers/delete! peer)
  (tap> peer)

  node2

  nil)
