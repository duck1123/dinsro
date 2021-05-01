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
    ::m.users/username])

(def identity-attribute ::m.users/username)

(def find-eid-by-username-query
  '[:find  ?eid
    :in    $ ?username
    :where [?eid ::m.users/username ?username]])

(def find-username-by-eid-query
  '[:find  ?username
    :in    $ ?eid
    :where [?eid ::m.users/username ?username]])

(>defn read-record
  [user-id]
  [:db/id => (? ::m.users/item)]
  (let [record (d/pull @db/*conn* attribute-list user-id)]
    (when (get record m.users/username)
      (dissoc record :db/id))))

(>defn read-record-by-eid
  [user-dbid]
  [:db/id => (? ::m.users/item)]
  (let [record (d/pull @db/*conn* attribute-list user-dbid)]
    (when (get record m.users/username)
      (dissoc record :db/id))))

(>defn read-records
  [ids]
  [(s/coll-of :db/id) => (s/coll-of ::m.users/item)]
  (map read-record-by-eid ids))

(>defn find-id-by-username
  [username]
  [::m.users/username => (? :db/id)]
  (ffirst (d/q find-eid-by-username-query @db/*conn* username)))

(>defn find-eid-by-username
  [username]
  [::m.users/username => :db/id]
  (ffirst (d/q find-eid-by-username-query @db/*conn* username)))

(>defn find-username-by-eid
  [eid]
  [:db/id => ::m.users/username]
  (ffirst (d/q find-username-by-eid-query @db/*conn* eid)))

(>defn find-by-username
  [username]
  [::m.users/username => (? ::m.users/item)]
  (when-let [id (find-id-by-username username)]
    (read-record id)))

(>defn create-record
  [params]
  [::m.users/params => :db/id]
  (if (nil? (find-id-by-username (m.users/username params)))
    (let [tempid   (d/tempid "user-id")
          params   (assoc params :db/id tempid)
          response (d/transact db/*conn* {:tx-data [params]})]
      (get-in response [:tempids tempid]))
    (throw (RuntimeException. "User already exists"))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.users/username _]] @db/*conn*)))

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
