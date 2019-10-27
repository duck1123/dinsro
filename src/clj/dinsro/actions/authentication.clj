(ns dinsro.actions.authentication
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.model.user :as model.user]
            [dinsro.specs :as specs]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :refer :all]
            [taoensso.timbre :as timbre]))

(defn-spec check-auth any?
  [email ::specs/email password ::specs/password]
  {:pre [(s/valid? ::specs/email email)]}
  (if-let [user (db/find-user-by-email {:email email})]
    (let [{:keys [password-hash]} user]
      (hashers/check password password-hash))))

(defn authenticate-handler
  [{{:keys [email password]} :params :keys [session]}]
  (if (check-auth email password)
    (assoc (ok {:identity email})
           :session (assoc session :identity email))
    (unauthorized)))

(defn register-handler
  "Register a user"
  [{:keys [params] :as request}]
  (if (s/valid? ::specs/register-request params)
    (do
      (model.user/create-user! params)
      (ok))
    (bad-request)))

(defn logout-handler
  [request]
  (assoc-in (ok {:identity nil}) [:session :identity] nil))
