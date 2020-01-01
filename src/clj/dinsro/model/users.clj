(ns dinsro.model.users
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.spec :as ds]
            [dinsro.spec.users :as s.users]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def attribute-list
  '[:db/id ::s.users/name ::s.users/email ::s.users/password-hash])

(def identity-attribute ::s.users/email)

(def find-by-email-query
  '[:find ?id
    :in $ ?email
    :where [?id ::s.users/email ?email]])

(defn prepare-record
  [params]
  (when-let [password (::s.users/password params)]
    (-> {::s.users/password-hash (hashers/derive password)}
        (merge params)
        (dissoc ::s.users/password))))

(s/fdef prepare-record
  :args (s/cat :params ::s.users/params)
  :ret (s/nilable ::s.users/item))

(defn read-record
  [user-id]
  (d/pull @db/*conn* attribute-list user-id))

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

(defn-spec find-by-email (s/nilable ::s.users/item)
  [email ::s.users/email]
  (when-let [id (find-id-by-email email)]
    (read-record id)))

(defn-spec create-record ::ds/id
  [params ::s.users/params]
  (if (nil? (find-id-by-email (::s.users/email params)))
    (let [tempid (d/tempid "user-id")
          record (assoc (prepare-record params) :db/id tempid)
          response (d/transact db/*conn* {:tx-data [record]})]
      (get-in response [:tempids tempid]))
    (throw (RuntimeException. "User already exists"))))

(defn-spec index-ids (s/coll-of ::ds/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.users/email _]] @db/*conn*)))

(defn index-records
  []
  (read-records (index-ids)))

(s/fdef index-records
  :args (s/cat)
  :ret (s/coll-of ::s.users/item))

(defn delete-record
  [user-id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity user-id]]})
  nil)

(s/fdef delete-record
  :args (s/cat :user-id ::ds/id)
  :ret nil?)

(defn delete-all
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(s/fdef delete-all
  :args (s/cat)
  :ret nil?)
