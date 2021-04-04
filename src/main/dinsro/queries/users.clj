(ns dinsro.queries.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.model.users :as m.users]
   [dinsro.specs]
   [taoensso.timbre :as timbre]))

(def attribute-list
  '[:db/id ::m.users/name ::m.users/email ::m.users/password-hash])

(def identity-attribute ::m.users/email)

(def find-by-email-query
  '[:find ?id
    :in $ ?email
    :where [?id ::m.users/email ?email]])

(>defn read-record
  [user-id]
  [:db/id => (? ::m.users/item)]
  (let [record (d/pull @db/*conn* attribute-list user-id)]
    (when (get record ::m.users/name)
      record)))

(>defn read-records
  [ids]
  [(s/coll-of :db/id) => (s/coll-of ::m.users/item)]
  (d/pull-many @db/*conn* attribute-list ids))

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
    (let [tempid (d/tempid "user-id")
          record (assoc params :db/id tempid)
          response (d/transact db/*conn* {:tx-data [record]})]
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
