^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.connections-notebook
  (:require
   [dinsro.queries.core.connections :as q.c.connections]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Connection Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment

  (q.c.connections/index-ids)

  nil)
