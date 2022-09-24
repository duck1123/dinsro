^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.actions.core.networks-notebook
  (:require
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.core.chains :as q.c.chains]
   [dinsro.queries.core.networks :as q.c.networks]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Core Network Actions

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
(nu/display-file-links)

(comment

  (q.c.chains/index-records)
  (q.c.chains/create-record {::m.c.chains/name "bitcoin"})

  (q.c.chains/find-by-name "bitcoin")

  (q.c.networks/index-records)

  (q.c.networks/create-record
   {::m.c.networks/name   "regtest"
    ::m.c.networks/chains (q.c.chains/find-by-name "bitcoin")})

  nil)
