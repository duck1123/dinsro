(ns dinsro.queries.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.components.datahike :as db]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [taoensso.timbre :as timbre]))

(def attribute-list
  '[:db/id
    ::m.users/password-hash
    ::m.users/id])

(def identity-attribute ::m.users/id)

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.users/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.users/id ?id]])

(>defn read-record
  [user-id]
  [:db/id => (? ::m.users/item)]
  (let [record (d/pull @db/*conn* attribute-list user-id)]
    (when (get record m.users/id)
      (dissoc record :db/id))))

(>defn read-record-by-eid
  [user-dbid]
  [:db/id => (? ::m.users/item)]
  (let [record (d/pull @db/*conn* attribute-list user-dbid)]
    (when (get record m.users/id)
      (dissoc record :db/id))))

(>defn read-records
  [ids]
  [(s/coll-of :db/id) => (s/coll-of ::m.users/item)]
  (map read-record-by-eid ids))

(>defn find-eid-by-id
  [id]
  [::m.users/id => (? :db/id)]
  (ffirst (d/q find-eid-by-id-query @db/*conn* id)))

(>defn find-id-by-eid
  [eid]
  [:db/id => (? ::m.users/id)]
  (ffirst (d/q find-id-by-eid-query @db/*conn* eid)))

(>defn find-by-id
  [id]
  [::m.users/id => (? ::m.users/item)]
  (when-let [eid (find-eid-by-id id)]
    (read-record eid)))

(>defn create-record
  [params]
  [::m.users/params => :db/id]
  (if (nil? (find-eid-by-id (m.users/id params)))
    (let [tempid   (d/tempid "user-id")
          params   (assoc params :db/id tempid)
          response (d/transact db/*conn* {:tx-data [params]})]
      (get-in response [:tempids tempid]))
    (throw (RuntimeException. "User already exists"))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.users/id _]] @db/*conn*)))

(>defn index-records
  []
  [=> (s/coll-of ::m.users/item)]
  (read-records (index-ids)))

(>defn delete-record
  [id]
  [:db/id => nil?]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(>defn delete-all
  []
  [=> nil?]
  (doseq [id (index-ids)]
    (delete-record id)))
