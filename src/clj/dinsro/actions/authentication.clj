(ns dinsro.actions.authentication
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.model.user :as m.users]
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

(s/def :register-handler/params (s/keys :req-un [::params]))
(s/def ::register-request (s/keys :req-un [:register-handler/params]))
(s/def :register-handler/body any?)
(s/def :register-handler/request (s/keys :req-un [:register-handler/body]))
(s/def ::register-handler-response (s/keys :req-un [:register-handler/body]))

(defn-spec register-handler ::register-handler-response
  "Register a user"
  [request :register-handler/request]
  (let [{:keys [params]} request]
    (if (s/valid? ::m.users/registration-params (timbre/spy :info params))
      (do
        (m.users/create-user! params)
        (ok))
      (bad-request))))

(defn logout-handler
  [request]
  (assoc-in (ok {:identity nil}) [:session :identity] nil))
