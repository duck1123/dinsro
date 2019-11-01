(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [dinsro.db.core :as db]
            [dinsro.specs :as ds]
            [taoensso.timbre :as timbre]))

(def schema
  [{:db/ident       :user/name
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

(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (if password
      (merge {:password-hash (hashers/derive password)}
             registration-data)
      nil)))

(defn create-user!
  [user-params]
  (if-let [user (prepare-user user-params)]
    (merge user (db/create-user! user))))

(defn read-user
  [user-id]
  (db/read-user {:id user-id}))

(defn list-users
  []
  (db/list-users))

(defn delete-user
  [user-id]
  (db/delete-user! {:id user-id}))
