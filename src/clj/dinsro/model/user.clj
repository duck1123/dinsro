(ns dinsro.model.user
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.specs :as ds]
            [taoensso.timbre :as timbre]))

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
