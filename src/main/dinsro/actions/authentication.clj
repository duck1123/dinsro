(ns dinsro.actions.authentication
  (:require
   [buddy.hashers :as hashers]
   [buddy.sign.jwt :as jwt]
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
   [io.pedestal.log :as log]
   [taoensso.encore :as enc]
   [tick.core :as t]))

(>defn get-user-id
  [env]
  [any? => (? ::m.users/id)]
  (some-> env :ring/request :session :identity))

(defn get-auth-data
  [user-id zone-id]
  (log/info :auth/get {:user-id user-id :zone-id zone-id})
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
    (log/info :session/associate {:s s})
    (fmw/augment-response
     (or response s)
     (fn [resp]
       (let [current-session (-> env :ring/request :session)
             merged (merge current-session s)]
         (log/info :session/merging {:current-session current-session
                                     :merged merged})
         (assoc resp :session merged))))))

(defn login!
  "Implementation of login. This is database-specific and is not further generalized for the demo."
  [env {:user/keys [username password]}]
  (log/info :login/begin {:username username})

  (enc/if-let [{::m.users/keys  [id #_name hashed-value salt iterations]
                :time-zone/keys [zone-id]} (queries/get-login-info env username)
               current-hashed-value (attr/encrypt password salt iterations)]
    (if (= hashed-value current-hashed-value)
      (do
        (log/info :login/success {:username username})
        (associate-session! env id zone-id nil))
      (do
        (log/error :login/failure {:username username})
        {::auth/provider :local
         ::auth/status   :failed}))
    (do
      (log/error :login/user-not-found {:username username})
      {::auth/provider :local
       ::auth/status   :failed})))

(defn logout!
  "Implementation of logout."
  [_env]
  (log/info :logout/started {})
  (fmw/augment-response
   {::auth/provider       :local
    :session/current-user nil
    :identity             nil
    ::auth/status         :not-logged-in}
   (fn [resp]
     (let [merged (-> resp
                      (assoc-in [:session :session/current-user] nil)
                      (assoc-in [:session :identity] nil))]
       (log/info :logout/merging {:session (:session resp) :merged merged})))))

(defn check-session!
  "get session from env"
  [env]
  (log/info :session/checking {})
  (or
   (some-> env :ring/request :session)
   {::auth/provider :local
    ::auth/status   :not-logged-in}))

(s/def ::login-response (s/keys))

(>defn authenticate
  "Check user authentication"
  [username password]
  [string? string? => (? (s/keys))]
  (if-let [user (q.users/find-by-id username)]
    (if-let [password-hash (::m.users/password-hash user)]
      (if (hashers/check password password-hash)
        (let [username (::m.users/id user)
              claims   {:user username
                        :exp  (t/>> (t/now) (t/new-duration 1 :days))}]
          {:identity username
           :token    (jwt/sign claims secret)})
        ;; Password does not match
        (do
          (log/info :password/not-matched {})
          nil))
      (do
        ;; No password, invalid user
        (log/info :password/missing {})
        nil))

    (do
      ;; User not found
      (log/info :user/not-found {})
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
          (log/error :user/already-exists {:exception ex})
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
         (log/error :registration/failed {:message "Failed to register" :exception ex})
         {::error true
          :ex     (str ex)})))))
