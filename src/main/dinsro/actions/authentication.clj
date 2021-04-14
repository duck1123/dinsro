(ns dinsro.actions.authentication
  (:require
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
   [clj-time.core :as time]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.users :as a.users]
   [dinsro.config :refer [secret]]
   [dinsro.queries.users :as q.users]
   [dinsro.model.users :as m.users]
   [dinsro.specs.actions.authentication :as s.a.authentication]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(>defn authenticate
  [email password]
  [string? string? => (? (s/keys))]
  (if-let [user (q.users/find-by-email email)]
    (if-let [password-hash (::m.users/password-hash user)]
      (if (hashers/check password password-hash)
        (let [id     (::m.users/id user)
              claims {:user id
                      :exp  (time/plus (time/now) (time/minutes 3600))}]
          {::s.a.authentication/identity id
           :token                        (jwt/sign claims secret)})
        ;; Password does not match
        (do
          (timbre/info "password not matched")
          nil))
      (do
        ;; No password, invalid user
        (timbre/info "no password")
        nil))

    (do
      ;; User not found
      (timbre/info "user not found")
      nil)))

(>defn authenticate-handler
  [request]
  [::s.a.authentication/authenticate-request => ::s.a.authentication/authenticate-response]
  (let [{{:keys [email password]} :params} request]
    (try
      (if (and (seq email) (seq password))
        (if-let [claim (authenticate email password)]
          (let [id (::s.a.authentication/identity claim)]
            (-> claim
                (http/ok)
                (assoc-in [:session :identity] id)))
          (http/unauthorized {:status :unauthorized}))
        (http/bad-request {:status :invalid}))
      (catch Exception ex
        (timbre/error ex "Failed to authenticate")))))

(>defn register
  [params]
  [::m.users/input-params => (? ::m.users/item)]
  (let [params (if-let [password (:password params)]
                 (assoc params ::m.users/password password)
                 params)
        params (dissoc params :password)
        params (a.users/prepare-record params)]
    (when (s/valid? ::m.users/params params)
      (try
        (let [id (q.users/create-record params)]
          (q.users/read-record id))
        (catch RuntimeException _
          (throw "User already exists")))
      #_(throw "Invalid"))))

(>defn register-handler
  "Register a user"
  [request]
  [::s.a.authentication/register-request => ::s.a.authentication/register-response]
  (let [{:keys [params]} request]
    (try
      (let [id (::m.users/id (register params))]
        (http/ok {:id id}))
      (catch RuntimeException _
        (http/bad-request {:status  :failed
                           :message "User already exists"})))))

(defn logout-handler
  [_]
  (assoc-in (http/ok {:identity nil}) [:session :identity] nil))
