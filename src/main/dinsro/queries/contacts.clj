(ns dinsro.queries.contacts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb :refer [concat-when]]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [xtdb.api :as xt]))

(def query-info
  {:ident   ::m.contacts/id
   :pk      '?contacts-id
   :clauses [[::m.users/id '?user-id]]
   :rules
   (fn [[user-id] rules]
     (->> rules
          (concat-when user-id
            [['?category-id ::m.contacts/user '?user-id]])))})

(defn count-ids
  ([] (count-ids {}))
  ([query-params] (c.xtdb/count-ids query-info query-params)))

(defn index-ids
  ([] (index-ids {}))
  ([query-params] (c.xtdb/index-ids query-info query-params)))

(>defn create-record
  "Create a contact record"
  [params]
  [::m.contacts/params => :xt/id]
  (let [node            (c.xtdb/get-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.contacts/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn read-record
  [id]
  [::m.contacts/id => (? ::m.contacts/item)]
  (let [db     (c.xtdb/get-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.contacts/id)
      (dissoc record :xt/id))))

(>defn find-by-user
  [user-id]
  [::m.contacts/user => (s/coll-of ::m.contacts/id)]
  (c.xtdb/query-values
   '{:find  [?id]
     :in    [[?user-id]]
     :where [[?id ::m.contacts/user ?user-id]]}
   [user-id]))

(defn delete!
  [id]
  (c.xtdb/delete! id))