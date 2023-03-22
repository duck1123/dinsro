^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.actions.ln.payments-notebook
  (:require
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.ln.payments :as q.ln.payments]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # LND Payment Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(comment

  (q.ln.payments/index-ids)

  (map q.ln.payments/delete!
       (q.ln.payments/index-ids))

  nil)
