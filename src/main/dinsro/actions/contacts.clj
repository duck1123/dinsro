(ns dinsro.actions.contacts
  (:require
   [dinsro.queries.contacts :as q.contacts]
   [taoensso.timbre :as log]))

;; [[../model/contacts.cljc]]
;; [[../mutations/contacts.cljc]]
;; [[../processors/contacts.clj]]
;; [[../queries/contacts.clj]]
;; [[../../../notebooks/dinsro/notebooks/contacts_notebook.clj]]

(defn create!
  [props]
  (log/info :create!/starting {:props props})
  (q.contacts/create! props))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.contacts/delete! id))
