(ns dinsro.queries.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
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
  (let [db     (c.crux/main-db)
        query  '{:find  [(pull ?eid [*])]
                 :in    [?eid]
                 :where [[?eid ::m.users/id ?name]]}
        result (crux/q db query user-id)
        record (ffirst result)]
    (when (get record ::m.users/id)
      (dissoc record :crux.db/id))))

(>defn read-record-by-eid
  [user-dbid]
  [:db/id => (? ::m.users/item)]
  (let [db     (c.crux/main-db)
        query  '{:find [(pull ?user [*])]
                 :in   [?user]}
        record (ffirst (crux/q db query user-dbid))]
    (when (get record ::m.users/id)
      (dissoc record :db/id))))

(>defn read-records
  [ids]
  [(s/coll-of :db/id) => (s/coll-of ::m.users/item)]
  (map read-record-by-eid ids))

(>defn find-eid-by-id
  [id]
  [::m.users/id => (? :db/id)]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-eid-by-id-query id))))

(>defn find-eid-by-name
  [name]
  [::m.users/name => (? :db/id)]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-eid-by-name-query name))))

(>defn find-id-by-eid
  [eid]
  [:db/id => (? ::m.users/id)]
  (let [db (c.crux/main-db)]
    (ffirst (crux/q db find-id-by-eid-query eid))))

(>defn find-by-id
  [id]
  [::m.users/id => (? ::m.users/item)]
  (when-let [eid (find-eid-by-id id)]
    (read-record eid)))

(>defn create-record
  [params]
  [::m.users/params => uuid?]
  (if (nil? (find-eid-by-name (::m.users/name params)))
    (let [node   (c.crux/main-node)
          id     (new-uuid)
          params (assoc params :crux.db/id id)
          params (assoc params ::m.users/id id)]
      (crux/await-tx node (crux/submit-tx node [[:crux.tx/put params]]))
      id)
    (throw (RuntimeException. "User already exists"))))

(>defn index-ids
  []
  [=> (s/coll-of ::m.users/id)]
  (let [db    (c.crux/main-db)
        query '{:find  [?e]
                :where [[?e ::m.users/id _]]}]
    (map first (crux/q db query))))

(>defn index-records
  []
  [=> (s/coll-of ::m.users/item)]
  (read-records (index-ids)))

(>defn delete-record
  [id]
  [:db/id => nil?]
  (let [node (c.crux/main-node)]
    (crux/await-tx node (crux/submit-tx node [[:crux.tx/delete id]]))
    nil))

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
