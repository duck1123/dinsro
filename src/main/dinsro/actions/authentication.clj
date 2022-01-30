(ns dinsro.actions.authentication
  (:require
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
   [clj-time.core :as time]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.server.api-middleware :as fmw]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.attributes :as attr]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.components.config :refer [secret]]
   [dinsro.components.database-queries :as queries]
   [dinsro.queries.users :as q.users]
   [dinsro.model.timezone :as timezone]
   [dinsro.model.users :as m.users]
   [taoensso.encore :as enc]
   [taoensso.timbre :as log]))

(>defn get-user-id
  [env]
  [any? => (? ::m.users/id)]
  (some-> env :ring/request :session :identity))

(defn get-auth-data
  [user-id zone-id]
  (log/info "get auth data")
  {:identity             user-id
   ::auth/provider       :local
   ::auth/status         :success
   :session/current-user (when user-id
                           (let [{::m.users/keys [name]} (q.users/read-record user-id)]
                             (assoc (m.users/ident user-id) ::m.users/name name)))
   :time-zone/zone-id    (-> zone-id :xt/id timezone/datomic-time-zones)})

(defn associate-session!
  [env id zone-id response]
  (let [s (get-auth-data id zone-id)]
    (fmw/augment-response
     (or response s)
     (fn [resp]
       (let [current-session (-> env :ring/request :session)]
         (assoc resp :session (merge current-session s)))))))

(defn login!
  "Implementation of login. This is database-specific and is not further generalized for the demo."
  [env {:user/keys [username password]}]
  (log/info "Attempt login for" username)

  (enc/if-let [{::m.users/keys  [id #_name hashed-value salt iterations]
                :time-zone/keys [zone-id]} (queries/get-login-info env username)
               current-hashed-value (attr/encrypt password salt iterations)]
    (if (= hashed-value current-hashed-value)
      (do
        (log/info "Login for" username)
        (associate-session! env id zone-id nil))
      (do
        (log/error "Login failure for" username)
        {::auth/provider :local
         ::auth/status   :failed}))
    (do
      (log/fatal "Login cannot find user" username)
      {::auth/provider :local
       ::auth/status   :failed})))

(defn logout!
  "Implementation of logout."
  [_env]
  (fmw/augment-response {} (fn [resp] (assoc resp :session {}))))

(defn check-session! [env]
  (log/info "Checking for existing session")
  (or
   (some-> env :ring/request :session)
   {::auth/provider :local
    ::auth/status   :not-logged-in}))

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
  (let [role (::m.users/role params)
        salt         (attr/gen-salt)
        iterations   100
        password     (::m.users/password params)
        hashed-value (attr/encrypt password salt iterations)
        params       (-> params
                         (dissoc ::m.users/password)
                         (assoc ::m.users/salt salt)
                         (assoc ::m.users/hashed-value hashed-value)
                         (assoc ::m.users/iterations iterations)
                         (assoc ::m.users/role (or role :acount.role/user)))]
    (when (or true (s/valid? ::m.users/params params))
      (try
        (let [id (q.users/create-record params)]
          (q.users/read-record id))
        (catch RuntimeException ex
          (log/error ex "User exists")
          (throw "User already exists"))))))

(>defn do-register
  "Register user with given name and password"
  ([name password]
   [::m.users/name ::m.users/password => (s/keys)]
   (do-register name password false))
  ([name password admin?]
   [::m.users/name ::m.users/password boolean? => (s/keys)]
   (let [params #::m.users{:password password :name name :role (if admin?
                                                                 :account.role/admin
                                                                 :account.role/user)}]

     (try
       (register params)
       (catch Exception ex
         (log/error "Failed to register" ex)
         {::error true
          :ex     (str ex)})))))
