(ns dinsro.queries.contacts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.specs]
   [lambdaisland.glogc :as log]
   [xtdb.api :as xt]))

(>defn create-record
  "Create a contact record"
  [params]
  [::m.contacts/params => :xt/id]
  (let [node            (c.xtdb/main-node)
        id              (new-uuid)
        prepared-params (-> params
                            (assoc ::m.contacts/id id)
                            (assoc :xt/id id))]
    (xt/await-tx node (xt/submit-tx node [[::xt/put prepared-params]]))
    id))

(>defn index-ids
  []
  [=> (s/coll-of ::m.contacts/id)]
  (log/info :index-ids/starting {})
  (c.xtdb/query-ids '{:find  [?id]
                      :where [[?id ::m.contacts/id _]]}))

(>defn read-record
  [id]
  [::m.contacts/id => (? ::m.contacts/item)]
  (let [db     (c.xtdb/main-db)
        record (xt/pull db '[*] id)]
    (when (get record ::m.contacts/id)
      (dissoc record :xt/id))))

(>defn find-by-user
  [user-id]
  [::m.contacts/user => (s/coll-of ::m.contacts/id)]
  (c.xtdb/query-ids
   '{:find  [?id]
     :in    [[?user-id]]
     :where [[?id ::m.contacts/user ?user-id]]}
   [user-id]))
