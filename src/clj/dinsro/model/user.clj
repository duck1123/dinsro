(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
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

(defn-spec prepare-user (s/nilable ::s.users/item)
  [params ::s.users/params]
  (when-let [password (::s.users/password params)]
    (-> {::s.users/password-hash (hashers/derive password)}
        (merge params)
        (dissoc ::s.users/password))))

(defn-spec create-user! :db/id
  [params ::s.users/params]
  (let [tempid (d/tempid "user-id")]
    (let [user (prepare-user (assoc params :db/id tempid))
          response (d/transact db/*conn* {:tx-data [user]})]
     (get-in response [:tempids tempid]))))

(defn-spec index-ids (s/coll-of :db/id)
  []
  (map first (d/q '[:find ?e :where [?e ::s.users/email _]] @db/*conn*)))

(defn-spec read-record (s/nilable ::s.users/item)
  [user-id :db/id]
  (d/pull @db/*conn*  user-id))

(defn-spec read-records (s/coll-of (s/nilable ::item))
  [ids (s/coll-of :ds/id)]
  (d/pull-many @db/*conn* attribute-list))

(defn-spec index-records (s/coll-of ::item)
  []
  (read-records (index-ids)))

(defn-spec delete-record any?
  [user-id :db/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity user-id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (index-ids)]
    (delete-record id)))

(defn-spec find-by-email (s/nilable ::s.users/item)
  [email ::s.users/email]
  (let [response (d/q find-by-email-query @db/*conn* email)
        id (ffirst response)]
    (read-record id)))

(defn-spec mock-record ::s.users/item
  []
  (let [params (gen/generate (s/gen ::s.users/params))
        id (create-user! params)]
    (read-record id)))

(comment
  (index-ids)

  (mock-user)
  (::s.users/email (first (list-users)))

  (find-by-email (::s.users/email (first (index-records))))
  )
