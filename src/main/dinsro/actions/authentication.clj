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
   [taoensso.timbre :as log]))

(>defn authenticate
  [username password]
  [string? string? => (? (s/keys))]
  (if-let [user (q.users/find-by-id username)]
    (if-let [password-hash (::m.users/password-hash user)]
      (if (hashers/check password password-hash)
        (let [username (::m.users/id user)
              claims   {:user username
                        :exp  (time/plus (time/now) (time/minutes 3600))}]
          {:identity username
           :token    (jwt/sign claims secret)})
        ;; Password does not match
        (do
          (log/info "password not matched")
          nil))
      (do
        ;; No password, invalid user
        (log/info "no password")
        nil))

    (do
      ;; User not found
      (log/info "user not found")
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
          (log/error ex "User exists")
          (throw "User already exists")))
      #_(throw "Invalid"))))
