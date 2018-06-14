(ns dinsro.actions.user.create-user
  (:require [crypto.password.bcrypt :as bcrypt]
            [dinsro.db.core :as db]
            [ring.util.http-response :refer :all]))

(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (merge {:password_hash (bcrypt/encrypt password)}
           registration-data)))

(defn create-user-response [registration-data]
  (let [user (prepare-user registration-data)]
    (db/create-user! user))
  (ok))
