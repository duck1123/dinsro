(ns dinsro.actions.core.chains
  (:require
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.queries.core.chains :as q.c.chains]))

(comment

  (q.c.chains/index-records)
  (q.c.chains/create-record {::m.c.chains/name "bitcoin"})

  (q.c.chains/find-id-by-name "bitcoin")

  nil)
