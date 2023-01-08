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
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :where [[?id ::m.contacts/id _]]}
        ids   (map first (xt/q db query))]
    (log/info :index-ids/finished {:ids ids})
    ids))

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
  (let [db    (c.xtdb/main-db)
        query '{:find  [?id]
                :in    [[?user-id ?name]]
                :where [[?id ::m.contacts/user ?user-id]]}
        response (xt/q db query [user-id])
        ids (map first response)]
    (log/finer :find-by-user/finished {:ids ids})
    ids))

(comment

  (read-record (first (index-ids)))

  nil)
