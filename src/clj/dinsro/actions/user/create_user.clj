(ns dinsro.actions.user.create-user
  (:require [clojure.spec.alpha :as s]
            [crypto.password.bcrypt :as bcrypt]
            [dinsro.db.core :as db]
            dinsro.specs
            [ring.util.http-response :refer :all]))

(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (if password
      (merge {:password_hash (bcrypt/encrypt password)}
             registration-data)
      nil)))

(defn create-user-response
  [{:keys [registration-data] :as request}]
  {:pre [(s/valid? :dinsro.specs/register-request registration-data)]}
  (if-let [user (prepare-user registration-data)]
    (do (db/create-user! user)
        (ok "ok"))))

(s/fdef create-user-response
  :args (s/cat :data :dinsro.specs/register-request))
