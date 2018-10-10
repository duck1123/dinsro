(ns dinsro.actions.authentication
  (:require [clojure.spec.alpha :as s]
            [crypto.password.bcrypt :as bcrypt]
            [dinsro.actions.user.create-user :refer [create-user-response]]
            [dinsro.db.core :as db]
            [dinsro.specs :as specs]
            [ring.util.http-response :refer :all]))

(defn authenticate
  [authentication-data]
  (let [{:keys [email password]} authentication-data]
    (let [user (db/read-user {:id 1})
          {:keys [password-hash]} user
          {:keys [password]} authentication-data
          hassed-password (bcrypt/check password password-hash)]
      (println hassed-password))
    (ok)))

(s/fdef authenticate
  :args (s/cat :authentication-data :dinsro.specs/authentication-data))

(defn register
  "Register a user"
  [data]
  {:pre [(s/valid? :dinsro.specs/register-request data)]}
  (create-user-response data)
  (ok))

(s/fdef register
  :args (s/cat :data :dinsro.specs/register-request))
