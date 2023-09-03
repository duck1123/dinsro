^{:nextjournal.clerk/visibility {:code :hide}}
(ns dinsro.notebooks.contacts-notebook
  (:require
   [dinsro.model.contacts :as m.contacts]
   [dinsro.notebook-utils :as nu]
   [dinsro.options.contacts :as o.contacts]
   [dinsro.queries.contacts :as q.contacts]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; [[../../../main/dinsro/actions/contacts.clj]]

;; # Contacts

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

;; ## Generated item

^{::clerk/viewer clerk/code}
(ds/gen-key ::m.contacts/item)

(def matt-odell "04c915daefee38317fa734444acee390a8269fe5810b2241e5e6dd343dfbecc9")

(def duck "47b38f4d3721390d5b6bef78dae3f3e3888ecdbf1844fbb33b88721d366d5c88")

(comment

  (q.contacts/index-ids)

  (q.users/find-by-name "alice")
  (q.users/find-by-name "bob")

  (q.contacts/find-by-user (q.users/find-by-name "alice"))
  (q.contacts/find-by-user (q.users/find-by-name "bob"))

  (q.contacts/create!
   {o.contacts/name   "Matt O'Dell"
    o.contacts/pubkey matt-odell
    o.contacts/user   (q.users/find-by-name "alice")})

  (q.contacts/create!
   {o.contacts/name   "Duck Nebuchadnezzar"
    o.contacts/pubkey duck
    o.contacts/user   (q.users/find-by-name "alice")})

  nil)
