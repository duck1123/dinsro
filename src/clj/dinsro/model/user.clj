(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre])
  (:import datahike.db.TxReport))

(s/def ::id pos-int?)
(s/def ::name string?)
(s/def ::email (s/with-gen #(and % (re-matches #".+@.+\..+" %)) (fn [] ds/email-gen)))
(s/def ::password string?)
(s/def ::password-hash string?)
(s/def ::registration-params (s/keys :req [::name ::email ::password]))
(s/def ::user (s/keys :req [::name ::email ::password-hash]))

(def schema
  [{:db/ident       ::id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::password-hash
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       ::email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn-spec init-schema TxReport
  []
  (d/transact db/*conn* schema))

(defn-spec prepare-user ::user
  [registration-data ::registration-params]
  (let [{:keys [dinsro.model.user/password]} registration-data]
    (if password
      (-> {::password-hash (hashers/derive password)}
          (merge registration-data)
          (dissoc ::password))
      nil)))

(defn-spec create-user! ::id
  [user-params ::registration-params]
  (let [tempid (d/tempid "user-id")
        user (prepare-user (assoc user-params :db/id tempid))
        response (d/transact db/*conn* {:tx-data [user]})]
    (get-in response [:tempids tempid])))

(defn list-user-ids
  []
  (map first (d/q '[:find ?e :where [?e ::email _]] @db/*conn*)))

(defn list-users
  []
  (->> (list-user-ids)
       (d/pull-many @db/*conn* '[::name ::email :db/id])))

(defn-spec delete-user any?
  [user-id ::id]
  (d/transact db/*conn* {:tx-data [[:db/retractEntity user-id]]}))

(defn-spec delete-all nil?
  []
  (doseq [id (list-user-ids)]
    (delete-user id)))

(defn-spec read-user ::user
  [user-id ::id]
  (d/pull @db/*conn* '[::name ::email ::password-hash] user-id))

(defn-spec find-by-email ::user
  [email ::email]
  (let [query '[:find ?id
                :in $ ?email
                :where [?id ::email ?email]]]
    (first (map (fn [[id]] (d/pull @db/*conn* '[:db/id ::name ::email ::password-hash] id))
                (d/q query @db/*conn* email)))))

(defn-spec mock-user ::user
  []
  (let [params (gen/generate (s/gen ::registration-params))
        id (create-user! params)]
    (read-user id)))
