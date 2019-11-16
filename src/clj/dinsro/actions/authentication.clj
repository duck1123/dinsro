(ns dinsro.actions.authentication
  (:require [clojure.spec.alpha :as s]
            [crypto.password.bcrypt :as bcrypt]
            [dinsro.actions.user.create-user :refer [create-user-response]]
            [dinsro.db.core :as db]
            [dinsro.model.user :as model.user]
            [dinsro.specs :as specs]
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn check-auth
  [email password]
  {:pre [(s/valid? ::specs/email email)]}
  (if-let [user (db/find-user-by-email {:email email})]
    (let [{:keys [password-hash]} user]
      (bcrypt/check password password-hash))))

(defn authenticate
  [request]
  (let [{{:keys [email password]} :params} request]
    (if (check-auth email password)
      (ok)
      (unauthorized))))

(defn register
  "Register a user"
  [{:keys [params] :as request}]
  (if (s/valid? ::specs/register-request params)
    (do
      (model.user/create-user! params)
      (ok))
    (bad-request)))
