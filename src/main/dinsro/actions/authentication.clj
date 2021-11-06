(ns dinsro.actions.authentication
  (:require
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
   [clj-time.core :as time]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]]
   [com.fulcrologic.rad.attributes :as attr]
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
  "Register user with params"
  [params]
  [::m.users/input-params => (? ::m.users/item)]
  (let [salt         (attr/gen-salt)
        iterations   100
        password     (::m.users/password params)
        hashed-value (attr/encrypt password salt iterations)
        params       (-> params
                         (dissoc ::m.users/password)
                         (assoc ::m.users/salt salt)
                         (assoc ::m.users/hashed-value hashed-value)
                         (assoc ::m.users/iterations iterations)
                         (assoc ::m.users/role :acount.role/user))]
    (when (or true (s/valid? ::m.users/params params))
      (try
        (let [id (q.users/create-record params)]
          (q.users/read-record id))
        (catch RuntimeException ex
          (log/error ex "User exists")
          (throw "User already exists"))))))

(>defn do-register
  "Register user with given name and password"
  [name password]
  [::m.users/name ::m.users/password => (s/keys)]
  (let [params #::m.users{:password password :name name}]
    (try
      (register params)
      (catch Exception ex
        (log/error "Failed to register" ex)
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
