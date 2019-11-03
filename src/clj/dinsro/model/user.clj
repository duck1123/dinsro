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

(s/def ::id number? #_(s/with-gen valid-uuid-str? uuid-str-gen))
(s/def ::name string?)
(s/def ::email (s/with-gen #(re-matches #".+@.+\..+" %) (fn [] ds/email-gen)))
(s/def ::password string? #_(s/and string? #(< 7 (count %))))
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
  (let [user (prepare-user user-params)
        response (d/transact db/*conn* {:tx-data [user]})]
    (get-in response [:tempids :db/current-tx])))

(defn list-user-ids
  []
  (map first (d/q '[:find ?e :where [?e ::email _]] @db/*conn*)))

(defn list-users
  []
  (->> (list-user-ids)
       (d/pull-many @db/*conn* '[*])))

(defn-spec delete-user any?
  [user-id ::id]
  (db/delete-user! {:id user-id}))

(defn-spec read-user ::user
  [user-id ::id]
  (d/pull @db/*conn* '[*] user-id))

(defn-spec find-by-email ::user
  [email ::email]
  (let [query '[:find ?id
                :in $ ?email
                :where [?id ::email ?email]]]
    (first (map (fn [[id]] (d/pull @db/*conn* '[*] id))
                (d/q query @db/*conn* email)))))
