(ns dinsro.actions.core.networks
  (:require
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.queries.core.chains :as q.c.chains]
   [dinsro.queries.core.networks :as q.c.networks]))

(comment

  (q.c.chains/index-records)
  (q.c.chains/create-record {::m.c.chains/name "bitcoin"})

  (q.c.chains/find-id-by-name "bitcoin")

  (q.c.networks/index-records)

  (q.c.networks/create-record
   {::m.c.networks/name "regtest"
    ::m.c.networks/chains (q.c.chains/find-id-by-name "bitcoin")})

  nil)
