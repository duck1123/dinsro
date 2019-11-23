(ns dinsro.actions.authentication
  (:require [buddy.hashers :as hashers]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.user :as m.users]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as specs]
            [expound.alpha :as expound]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(s/def :register-handler-optional/params
  (s/keys :opt-un [::s.users/name ::s.users/email ::s.users/password]))
(s/def :register-handler/params
  (s/keys :req-un [::s.users/name ::s.users/email ::s.users/password]))

(s/def ::register-request (s/keys :req-un [:register-handler-optional/params]))
(s/def ::register-request-valid (s/keys :req-un [:register-handler/params]))

(s/def :register-handler/body any?)
(s/def :register-handler/request (s/keys :req-un [:register-handler/body]))
(s/def ::register-handler-response (s/keys :req-un [:register-handler/body]))

(s/def ::authenticate-handler-request (s/keys))

(def param-rename-map
  {:name     ::s.users/name
   :email    ::s.users/email
   :password ::s.users/password})

(defn-spec check-auth (s/nilable boolean?)
  [email ::s.users/email password ::s.users/password]
  (if-let [user (m.users/find-by-email email)]
    (let [{:keys [dinsro.spec.users/password-hash]} user]
      (hashers/check password password-hash))))

(defn-spec authenticate-handler any?
  [request ::authenticate-handler-request]
  (let [{{:keys [email password]} :params} (timbre/spy :info request)]
    (if (check-auth email password)
      (-> {:identity email}
          (http/ok)
          (assoc-in [:session :identity] email))
      (http/unauthorized {:status :unathorized}))))

(defn-spec register-handler ::register-handler-response
  "Register a user"
  [request ::register-request]
  (let [{:keys [params]} request
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (if (s/valid? ::s.users/params params)
      (let [id (m.users/create-record params)]
        (http/ok {:id id}))
      (http/bad-request {:status :failed}))))

(defn logout-handler
  [request]
  (assoc-in (http/ok {:identity nil}) [:session :identity] nil))
