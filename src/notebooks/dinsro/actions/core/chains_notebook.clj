^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.chains-notebook
  (:require
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.queries.core.chains :as q.c.chains]
   [dinsro.notebook-utils :as nu]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Chain Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment

  (q.c.chains/index-records)
  (q.c.chains/create-record {::m.c.chains/name "bitcoin"})

  (q.c.chains/find-id-by-name "bitcoin")

  nil)
