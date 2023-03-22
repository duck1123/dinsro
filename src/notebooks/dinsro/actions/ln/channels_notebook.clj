^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.actions.ln.channels-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.ln.channels :as q.ln.channels]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Channel Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; ## delete!

(comment

  (map q.ln.channels/delete! (q.ln.channels/index-ids))

  nil)

(comment
  (q.ln.channels/index-ids)

  nil)
