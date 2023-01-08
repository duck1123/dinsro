(ns dinsro.actions.contacts
  (:require
   [dinsro.model.contacts :as m.contacts]
   [dinsro.queries.contacts :as q.contacts]
   [dinsro.queries.users :as q.users]))

(def matt-odell "04c915daefee38317fa734444acee390a8269fe5810b2241e5e6dd343dfbecc9")

(def duck "47b38f4d3721390d5b6bef78dae3f3e3888ecdbf1844fbb33b88721d366d5c88")

(comment

  (q.contacts/index-ids)

  (q.users/find-by-name "alice")
  (q.users/find-by-name "bob")

  (q.contacts/find-by-user (q.users/find-by-name "alice"))
  (q.contacts/find-by-user (q.users/find-by-name "bob"))

  (q.contacts/create-record
   {::m.contacts/name "Matt O'Dell"
    ::m.contacts/pubkey matt-odell
    ::m.contacts/user   (q.users/find-by-name "alice")})

  (q.contacts/create-record
   {::m.contacts/name   "Duck Nebuchadnezzar"
    ::m.contacts/pubkey duck
    ::m.contacts/user   (q.users/find-by-name "alice")})

  nil)
