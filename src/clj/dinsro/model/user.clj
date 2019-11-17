(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.specs :as ds]
            [dinsro.specs.users :as s.users]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre])
  (:import datahike.db.TxReport))

(defn-spec init-schema TxReport
  []
  (d/transact db/*conn* schema))

(defn-spec prepare-user ::s.users/items
  [registration-data ::s.users/params]
  (let [{:keys [dinsro.spec.users/password]} registration-data]
    (if password
      (-> {::s.users/password-hash (hashers/derive password)}
          (merge registration-data)
          (dissoc ::s.users/password))
      nil)))

(defn-spec create-user! :db/id
  [user-params ::s.users/params]
  (let [tempid (d/tempid "user-id")
        user (prepare-user (assoc user-params :db/id tempid))
        response (d/transact db/*conn* {:tx-data [user]})]
    (get-in response [:tempids tempid])))

(defn list-user-ids
  []
  (map first (d/q '[:find ?e :where [?e ::s.users/email _]] @db/*conn*)))

(defn list-users
  []
  (->> (list-user-ids)
       (d/pull-many @db/*conn* '[::s.users/name ::s.users/email :db/id])))

(defn-spec delete-user any?
  [user-id :db/id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity user-id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (list-user-ids)]
    (delete-user id)))

(defn-spec read-user (s/nilable ::s.users/item)
  [user-id ::id]
  (d/pull @db/*conn* '[::s.users/name ::s.users/email ::s.users/password-hash] user-id))

(defn-spec find-by-email (s/nilable ::s.users/item)
  [email ::s.users/email]
  (let [query '[:find ?id
                :in $ ?email
                :where [?id ::s.users/email ?email]]]
    (first (map read-user (d/q query @db/*conn* email)))))

(defn-spec mock-user ::user
  []
  (let [params (gen/generate (s/gen ::s.users/params))
        id (create-user! params)]
    (read-user id)))
