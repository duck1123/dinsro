(ns dinsro.actions.authentication
  (:require [clojure.spec.alpha :as s]
            [crypto.password.bcrypt :as bcrypt]
            [dinsro.actions.user.create-user :refer [create-user-response]]
            [dinsro.db.core :as db]
            [dinsro.specs :as specs]
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn check-auth
  [email password]
  {:pre [(s/valid? ::specs/email email)]}
  (if-let [user (db/find-user-by-email {:email email})]
    (let [{:keys [password-hash]} user]
      (bcrypt/check password password-hash))))

(s/fdef check-auth
  :args (s/cat :email ::specs/email))

(defn authenticate
  [request]
  (let [{{:keys [email password]} :params} request]
    (if (check-auth email password)
      (ok)
      (unauthorized))))

(s/fdef authenticate
  :args (s/cat :authentication-data :dinsro.specs/authentication-data))

(defn register
  "Register a user"
  [data]
  ;; {:pre [(s/valid? :dinsro.specs/register-request data)]}
  (create-user-response (s/assert :dinsro.specs/register-request data))
  (ok))

(s/fdef register
  :args (s/cat :data :dinsro.specs/register-request))
