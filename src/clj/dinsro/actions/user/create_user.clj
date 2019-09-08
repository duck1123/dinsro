(ns dinsro.actions.user.create-user
  (:require [clojure.spec.alpha :as s]
            [crypto.password.bcrypt :as bcrypt]
            [dinsro.db.core :as db]
            dinsro.specs
            [ring.util.http-response :refer :all]))

(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (merge {:password_hash (bcrypt/encrypt password)}
           registration-data)))

(defn create-user-response [registration-data]
  {:pre [(s/valid? :dinsro.specs/register-request registration-data)]}
  (let [user (prepare-user registration-data)]
    (db/create-user! user))
  (ok))

(s/fdef create-user-response
  :args (s/cat :data :dinsro.specs/register-request))
