(ns dinsro.queries.contacts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.options.contacts :as o.contacts]
   [dinsro.options.users :as o.users]))

;; [[../actions/contacts.clj]]
;; [[../../../notebooks/dinsro/notebooks/contacts_notebook.clj]]

(def model-key o.contacts/id)

(def query-info
  {:ident   model-key
   :pk      '?contacts-id
   :clauses [[o.users/id '?user-id]]
   :rules
   (fn [[user-id] rules]
     (->> rules
          (concat-when user-id
            [['?category-id o.contacts/user '?user-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create!
  "Create a contact record"
  [params]
  [::m.contacts/params => :xt/id]
  (c.xtdb/create! model-key params))

(>defn read-record
  [id]
  [::m.contacts/id => (? ::m.contacts/item)]
  (c.xtdb/read model-key id))

(>defn find-by-user
  [user-id]
  [::m.contacts/user => (s/coll-of ::m.contacts/id)]
  (c.xtdb/query-values
   '{:find  [?id]
     :in    [[?user-id]]
     :where [[?id o.contacts/user ?user-id]]}
   [user-id]))

(defn delete!
  [id]
  (c.xtdb/delete! id))
