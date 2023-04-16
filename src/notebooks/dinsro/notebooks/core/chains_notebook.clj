^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.core.chains-notebook
  (:require
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.chains :as q.c.chains]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Chain Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(comment

  (q.c.chains/create-record {::m.c.chains/name "bitcoin"})

  (q.c.chains/find-by-name "bitcoin")

  nil)
