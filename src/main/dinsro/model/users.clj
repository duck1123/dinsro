(ns dinsro.model.users
  (:require
   [clojure.spec.alpha :as s]
   [datahike.api :as d]
   [dinsro.db :as db]
   [dinsro.specs :as ds]
   [dinsro.specs.users :as s.users]
   [taoensso.timbre :as timbre]))

(def attribute-list
  '[:db/id ::s.users/name ::s.users/email ::s.users/password-hash])

(def identity-attribute ::s.users/email)

(def find-by-email-query
  '[:find ?id
    :in $ ?email
    :where [?id ::s.users/email ?email]])

(defn read-record
  [user-id]
  (let [record (d/pull @db/*conn* attribute-list user-id)]
    (when (get record ::s.users/name)
      record)))

(s/fdef read-record
  :args (s/cat :user-id :db/id)
  :ret (s/nilable ::s.users/item))

(defn read-records
  [ids]
  (d/pull-many @db/*conn* attribute-list ids))

(s/fdef read-records
  :args (s/cat :ids (s/coll-of ::ds/id))
  :ret (s/coll-of (s/nilable ::s.users/item)))

(defn find-id-by-email
  [email]
  (ffirst (d/q find-by-email-query @db/*conn* email)))

(s/fdef find-id-by-email
  :args (s/cat :email ::s.users/email)
  :ret  (s/nilable ::ds/id))

(defn find-by-email
  [email]
  (when-let [id (find-id-by-email email)]
    (read-record id)))

(s/fdef find-by-email
  :args (s/cat :email ::s.users/email)
  :ret (s/nilable ::s.users/item))

(defn create-record
  [params]
  (if (nil? (find-id-by-email (::s.users/email params)))
    (let [tempid (d/tempid "user-id")
          record (assoc params :db/id tempid)
          response (d/transact db/*conn* {:tx-data [record]})]
      (get-in response [:tempids tempid]))
    (throw (RuntimeException. "User already exists"))))

(s/fdef create-record
  :args (s/cat :params ::s.users/params)
  :ret ::ds/id)

(defn index-ids
  []
  (map first (d/q '[:find ?e :where [?e ::s.users/email _]] @db/*conn*)))

(s/fdef index-ids
  :args (s/cat)
  :ret (s/coll-of ::ds/id))

(defn index-records
  []
  (read-records (index-ids)))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::s.users/item))

(defn delete-record
  [id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity id]]})
  nil)

(s/fdef delete-record
  :args (s/cat :id ::ds/id)
  :ret nil?)

(defn delete-all
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(s/fdef delete-all
  :args (s/cat)
  :ret nil?)
