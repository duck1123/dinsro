(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(def schema
  [{:db/ident       :user/id
    :db/valueType   :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident       :user/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :user/password-hash
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident       :user/email
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn init-schema
  []
  (d/transact db/*conn* schema))

(defn-spec prepare-user any?
  [registration-data ::ds/register-request]
  (let [{:keys [password]} registration-data]
    (if password
      (merge {:password-hash (hashers/derive password)}
             registration-data)
      nil)))

(defn-spec create-user! ::ds/id
  [user-params ::ds/register-request]
  (let [response (d/transact db/*conn* {:tx-data [user-params]})]
    (get-in response [:tempids :db/current-tx])))

(defn list-user-ids
  []
  (d/q '[:find ?e :where [?e :user/email _]] @db/*conn*))

(defn list-users
  []
  (->> (list-user-ids)
       (map first)
       (d/pull-many @db/*conn* '[*])))

(defn-spec delete-user any?
  [user-id ::ds/id]
  (db/delete-user! {:id user-id}))

(defn-spec read-user ::ds/user
  [user-id ::ds/id]
  (let [query '[:find ?e
                   :in $ ?user-id
                   :where [?e :user/id ?user-id]]
           response (d/q query @db/*conn* user-id)]
    (when-let [uid (ffirst response)]
     (d/pull @db/*conn* '[*] uid))))
