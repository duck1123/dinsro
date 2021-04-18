(ns dinsro.queries.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [dinsro.utils :as utils]
   [taoensso.timbre :as timbre]))

(def attribute-list
  '[:db/id
    ::m.users/email
    ::m.users/id
    ::m.users/name
    ::m.users/password-hash])

(def identity-attribute ::m.users/email)

(def find-by-email-query
  '[:find  ?eid
    :in    $ ?email
    :where [?eid ::m.users/email ?email]])

(def find-eid-by-id-query
  '[:find  ?eid
    :in    $ ?id
    :where [?eid ::m.users/id ?id]])

(def find-id-by-eid-query
  '[:find  ?id
    :in    $ ?eid
    :where [?eid ::m.users/id ?id]])

(>defn find-eid-by-id
  [id]
  [::m.users/id => :db/id]
  (ffirst (d/q find-eid-by-id-query @db/*conn* id)))

(>defn find-id-by-eid
  [eid]
  [:db/id => ::m.users/id]
  (ffirst (d/q find-id-by-eid-query @db/*conn* eid)))

(>defn read-record
  [user-id]
  [:db/id => (? ::m.users/item)]
  (let [record (d/pull @db/*conn* attribute-list user-id)]
    (when (get record m.users/name)
      (dissoc record :db/id))))

(>defn read-record-by-eid
  [user-dbid]
  [:db/id => (? ::m.users/item)]
  (let [record (d/pull @db/*conn* attribute-list user-dbid)]
    (when (get record m.users/name)
      (dissoc record :db/id))))

(>defn read-record-by-id
  [id]
  [::m.users/id => (? ::m.users/item)]
  (let [eid (find-eid-by-id id)]
    (read-record-by-eid eid)))

(>defn read-records
  [ids]
  [(s/coll-of :db/id) => (s/coll-of ::m.users/item)]
  (map read-record-by-eid ids))

(>defn find-id-by-email
  [email]
  [::m.users/email => (? :db/id)]
  (ffirst (d/q find-by-email-query @db/*conn* email)))

(>defn find-by-email
  [email]
  [::m.users/email => (? ::m.users/item)]
  (when-let [id (find-id-by-email email)]
    (read-record id)))

(>defn create-record
  [params]
  [::m.users/params => :db/id]
  (if (nil? (find-id-by-email (::m.users/email params)))
    (let [tempid   (d/tempid "user-id")
          params   (assoc params ::m.users/id (utils/uuid))
          params   (assoc params :db/id tempid)
          response (d/transact db/*conn* {:tx-data [params]})]
      (get-in response [:tempids tempid]))
    (throw (RuntimeException. "User already exists"))))

(>defn index-ids
  []
  [=> (s/coll-of :db/id)]
  (map first (d/q '[:find ?e :where [?e ::m.users/email _]] @db/*conn*)))

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
