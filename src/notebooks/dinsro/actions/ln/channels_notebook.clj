^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.ln.channels-notebook
  (:require
   [dinsro.queries.ln.channels :as q.ln.channels]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Channel Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment
  (q.ln.channels/index-ids)
  (first (q.ln.channels/index-records))

  (q.ln.channels/index-records)

  (map q.ln.channels/delete! (q.ln.channels/index-ids))

  nil)
