(ns user
  (:require
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.components.database-queries :as dq]
   [dinsro.mocks :as mocks]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.users :as q.users]
   [taoensso.timbre :as log]))

(comment
  (mocks/mock-account)

  (:main c.crux/crux-nodes)

  (def db (crux/db (:main c.crux/crux-nodes)))

  (q.accounts/index-ids)
  (q.accounts/index-records)

  (q.categories/index-ids)

  (q.users/index-records)

  (crux/q db '{:find [?uuid] :where [[?uuid ::m.accounts/id ?id]]})

  (crux/q
   db
   '{:find [?uuid ?name]
     :where [[?uuid ::m.navlink/id ?id]
             [?uuid ::m.navlink/name ?name]
             [?uuid ::m.navlink/href ?href]]})

  (crux/q
   db
   '{:find  [(pull ?uuid [*])]
     :where [[?uuid ::m.navlink/id ?id]
             [?uuid ::m.navlink/name ?name]
             [?uuid ::m.navlink/href ?href]]})

  (crux/q
   db
   '{:find  [(pull ?uuid [*])]
     :in [[?id ...]]
     :where [[?uuid ::m.navlink/id ?id]
             [?uuid ::m.navlink/name ?name]
             [?uuid ::m.navlink/href ?href]]}
   ["admin"
    "login"])

  (dq/get-navlinks- db ["admin" "login"])

  (let [id nil]
    (q.categories/find-eid-by-id id))

  (dq/get-all-categories- db)
  (dq/get-all-currencies- db)
  (dq/get-all-navlinks- db)
  (dq/get-navlinks- db ["admin"])

  (crux/submit-tx
   (:main c.crux/crux-nodes)
   [[:crux.tx/put {:crux.db/id      :a
                   ::m.navlink/id   :a
                   ::m.navlink/name "A"}]]))
