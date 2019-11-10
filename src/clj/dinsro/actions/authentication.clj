(ns dinsro.actions.authentication
  (:require [buddy.hashers :as hashers]
            [clojure.spec.alpha :as s]
            [dinsro.model.user :as m.users]
            [dinsro.specs :as specs]
            [expound.alpha :as expound]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :register-handler-optional/params
  (s/keys :opt-un [::m.users/name ::m.users/email ::m.users/password]))
(s/def :register-handler/params
  (s/keys :req-un [::m.users/name ::m.users/email ::m.users/password]))

(s/def ::register-request (s/keys :req-un [:register-handler-optional/params]))
(s/def ::register-request-valid (s/keys :req-un [:register-handler/params]))

(s/def :register-handler/body any?)
(s/def :register-handler/request (s/keys :req-un [:register-handler/body]))
(s/def ::register-handler-response (s/keys :req-un [:register-handler/body]))

(defn-spec check-auth boolean?
  [email ::m.users/email password ::m.users/password]
  (if-let [user (m.users/find-by-email email)]
    (let [{:keys [dinsro.model.user/password-hash]} user]
      (hashers/check password password-hash))))

(defn-spec authenticate-handler any?
  [request any?]
  (let [{{:keys [email password]} :params :keys [session]} request]
    (if (check-auth email password)
      (assoc (http/ok {:identity email})
             :session (assoc session :identity email))
      (http/unauthorized {:status :unathorized}))))

(defn-spec register-handler ::register-handler-response
  "Register a user"
  [request ::register-request]
  (let [{{:keys [name email password]} :params} request
        params {::m.users/name name
                ::m.users/email email
                ::m.users/password password}]
    (if (s/valid? ::m.users/registration-params params)
      (do
        (m.users/create-user! params)
        (http/ok {:id (m.users/create-user! params)}))
      (do
        #_(expound/expound :register-handler-optional/params (timbre/spy :info params))
        (http/bad-request {:status :failed})))))

(defn logout-handler
  [request]
  (assoc-in (http/ok {:identity nil}) [:session :identity] nil))
