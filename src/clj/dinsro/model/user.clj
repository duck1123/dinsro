(ns dinsro.model.user
  (:require [clojure.spec.alpha :as s]
            [crypto.password.bcrypt :as bcrypt]
            [dinsro.db.core :as db]
            dinsro.specs
            [taoensso.timbre :as timbre]))

(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (if password
      (merge {:password-hash (bcrypt/encrypt password)}
             registration-data)
      nil)))

(defn create-user!
  [params]
  (if-let [user (prepare-user params)]
    (db/create-user! user)))
