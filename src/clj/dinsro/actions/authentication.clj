(ns dinsro.actions.authentication
  (:require [buddy.hashers :as hashers]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [dinsro.model.users :as m.users]
            [dinsro.spec.actions.authentication :as s.a.authentication]
            [dinsro.spec.users :as s.users]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name     ::s.users/name
   :email    ::s.users/email
   :password ::s.users/password})

(defn-spec check-auth (s/nilable boolean?)
  [email ::s.users/email password ::s.users/password]
  )

(defn-spec authenticate-handler any?
  [request ::s.a.authentication/authenticate-handler-request]
  (let [{{:keys [email password]} :params} request]
    (if (and (seq email) (seq password))
      (if-let [user (m.users/find-by-email email)]
        (if-let [password-hash (::s.users/password-hash user)]
          (if (hashers/check password password-hash)
            (let [id (:db/id user)]
              (-> {::s.a.authentication/identity id}
                  (http/ok)
                  (assoc-in [:session :identity] id)))
            ;; Password does not match
            (http/unauthorized {:status :unauthorized}))
          ;; No password, invalid user
          (http/unauthorized {:status :unauthorized}))
        ;; User not found
        (http/unauthorized {:status :unauthorized}))
      (http/bad-request {:status :invalid}))))

(defn-spec register-handler ::s.a.authentication/register-handler-response
  "Register a user"
  [request ::s.a.authentication/register-request]
  (let [{:keys [params]} request
        params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (if (s/valid? ::s.users/params params)
      (let [id (m.users/create-record params)]
        (http/ok {:id id}))
      (http/bad-request {:status :failed}))))

(defn logout-handler
  [_]
  (assoc-in (http/ok {:identity nil}) [:session :identity] nil))
