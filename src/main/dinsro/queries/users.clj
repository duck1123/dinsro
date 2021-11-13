(ns dinsro.queries.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [xtdb.api :as xt]
   [dinsro.components.xtdb :as c.xtdb]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [taoensso.timbre :as log]))

(def identity-attribute ::m.users/id)

(def find-eid-by-id-query
  '{:find  [?eid]
    :in    [?id]
    :where [[?eid ::m.users/id ?id]]})

(def find-eid-by-name-query
  '{:find  [?eid]
    :in    [?name]
    :where [[?eid ::m.users/name ?name]]})

(def find-id-by-eid-query
  '{:find  [?id]
    :in    [?eid]
    :where [[?eid ::m.users/id ?id]]})

(>defn read-record
  [user-id]
  [uuid? => (? ::m.users/item)]
  (let [db     (c.xtdb/main-db)
        query  '{:find  [(pull ?eid [*])]
                 :in    [?eid]
                 :where [[?eid ::m.users/id ?name]]}
        result (xt/q db query user-id)
        record (ffirst result)]
    (when (get record ::m.users/id)
      (dissoc record :xt/id))))

(>defn read-record-by-eid
  [user-dbid]
  [:xt/id => (? ::m.users/item)]
  (let [db     (c.xtdb/main-db)
        query  '{:find [(pull ?user [*])]
                 :in   [?user]}
        record (ffirst (xt/q db query user-dbid))]
    (when (get record ::m.users/id)
      (dissoc record :xt/id))))

(>defn read-records
  [ids]
  [(s/coll-of :xt/id) => (s/coll-of ::m.users/item)]
  (map read-record-by-eid ids))

(>defn find-eid-by-id
  [id]
  [::m.users/id => (? ::m.users/id)]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-eid-by-id-query id))))

(>defn find-eid-by-name
  [name]
  [::m.users/name => (? ::m.users/id)]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-eid-by-name-query name))))

(>defn find-id-by-eid
  [eid]
  [:xt/id => (? ::m.users/id)]
  (let [db (c.xtdb/main-db)]
    (ffirst (xt/q db find-id-by-eid-query eid))))

(>defn find-by-id
  [id]
  [::m.users/id => (? ::m.users/item)]
  (when-let [eid (find-eid-by-id id)]
    (read-record eid)))

(>defn create-record
  "Create a user record"
  [params]
  [::m.users/params => ::m.users/id]
  (if (nil? (find-eid-by-name (::m.users/name params)))
    (let [node   (c.xtdb/main-node)
          id     (new-uuid)
          params (assoc params :xt/id id)
          params (assoc params ::m.users/id id)]
      (xt/await-tx node (xt/submit-tx node [[::xt/put params]]))
      id)
    (throw (RuntimeException. "User already exists"))))

(>defn index-ids
  "list all user ids"
  []
  [=> (s/coll-of ::m.users/id)]
  (let [db    (c.xtdb/main-db)
        query '{:find  [?e]
                :where [[?e ::m.users/id _]]}]
    (map first (xt/q db query))))

(>defn index-records
  "list all users"
  []
  [=> (s/coll-of ::m.users/item)]
  (read-records (index-ids)))

(>defn delete-record
  "delete user by id"
  [id]
  [:xt/id => nil?]
  (let [node (c.xtdb/main-node)]
    (xt/await-tx node (xt/submit-tx node [[::xt/delete id]]))
    nil))

(>defn delete-all
  "delete all users"
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
