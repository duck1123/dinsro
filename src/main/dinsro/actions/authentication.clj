(ns dinsro.actions.authentication
  (:require
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
   [clj-time.core :as time]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
   [dinsro.actions.users :as a.users]
   [dinsro.config :refer [secret]]
   [dinsro.queries.users :as q.users]
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as log]))

(s/def ::login-response (s/keys))

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

(>defn do-register
  [name password]
  [::m.users/name ::m.users/password => (s/keys)]
  (let [params #::m.users{:password password :name name}]
    (try
      (register params)
      (catch Exception ex
        {::error true
         :ex     (str ex)}))))

(>defn do-login
  [session username password]
  [any? ::m.users/name ::m.users/password => ::login-response]
  (if-let [user-id (q.users/find-eid-by-name username)]
    (if (= password m.users/default-password)
      (let [response {:user/username            username
                      :session/current-user-ref {::m.users/id user-id}
                      :user/valid?              true}
            handler  (fn [ring-response]
                       (assoc ring-response :session (assoc session :identity username)))]
        (augment-response response handler))
      {:user/username nil
       :user/valid?   false})
    {:user/username nil
     :user/valid?   false}))
