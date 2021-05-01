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
   [taoensso.timbre :as timbre]))

(>defn authenticate
  [username password]
  [string? string? => (? (s/keys))]
  (if-let [user (q.users/find-by-username username)]
    (if-let [password-hash (::m.users/password-hash user)]
      (if (hashers/check password password-hash)
        (let [username (::m.users/username user)
              claims   {:user username
                        :exp  (time/plus (time/now) (time/minutes 3600))}]
          {::s.a.authentication/identity username
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
        (catch RuntimeException ex
          (timbre/error ex "User exists")
          (throw "User already exists")))
      #_(throw "Invalid"))))
