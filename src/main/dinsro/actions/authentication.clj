(ns dinsro.actions.authentication
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.server.api-middleware :as fmw]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [com.fulcrologic.rad.attributes :as attr]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.components.database-queries :as queries]
   [dinsro.model.timezone :as timezone]
   [dinsro.model.users :as m.users]
   [dinsro.queries.users :as q.users]
   [lambdaisland.glogc :as log]
   [taoensso.encore :as enc]))

(>defn get-user-id
  [env]
  [any? => (? ::m.users/id)]
  (some-> env :ring/request :session :identity))

(defn get-auth-data
  [user-id zone-id]
  (log/info :get-auth-data/starting {:user-id user-id :zone-id zone-id})
  (let [current-user (when user-id
                       (let [{::m.users/keys [name]} (q.users/read-record user-id)]
                         (assoc (m.users/ident user-id) ::m.users/name name)))
        zone-id      (-> zone-id :xt/id timezone/datomic-time-zones)
        data         {:identity             user-id
                      ::auth/provider       :local
                      ::auth/status         :success
                      :time-zone/zone-id    zone-id}]
    (log/info :get-auth-data/finished {:data data :current-user current-user})
    data))

(defn associate-session!
  [env user-id zone-id response]
  (let [auth-data (get-auth-data user-id zone-id)]
    (log/info :associate-session/starting {:auth-data auth-data :response response})
    (fmw/augment-response
     (let [current-user (when user-id
                          (let [{::m.users/keys [name]} (q.users/read-record user-id)]
                            (assoc (m.users/ident user-id) ::m.users/name name)))]
       (or response (assoc auth-data :session/current-user current-user)))
     (fn [resp]
       (log/info :associate-session/handling {:resp resp :auth-data auth-data})
       (let [current-session (-> env :ring/request :session)
             merged (merge current-session auth-data)]
         (log/info :associate-session!/merged {:current-session current-session :merged merged})
         (assoc resp :session merged))))))

(defn login!
  "Implementation of login. This is database-specific and is not further generalized for the demo."
  [env {:user/keys [username password]}]
  (log/info :login!/starting {:username username})

  (enc/if-let [{::m.users/keys  [id #_name hashed-value salt iterations]
                :time-zone/keys [zone-id]} (queries/get-login-info env username)
               current-hashed-value (attr/encrypt password salt iterations)]
    (if (= hashed-value current-hashed-value)
      (do
        (log/info :login!/success {:username username})
        (let [response (associate-session! env id zone-id nil)]
          (log/info :login!/finished {:response response})
          response))
      (do
        (log/error :login!/failure {:username username})
        {::auth/provider :local
         ::auth/status   :failed}))
    (do
      (log/error :login!/user-not-found {:username username})
      {::auth/provider :local
       ::auth/status   :failed})))

(defn logout!
  "Implementation of logout."
  [env]
  (log/info :logout!/starting {})
  (fmw/augment-response
   {::auth/provider       :local
    :session/current-user nil
    :identity             nil
    ::auth/status         :not-logged-in}
   (fn [resp]
     (log/info :logout!/handler {:resp resp})
     (let [session (some-> env :ring/request :session)
           merged  (assoc session :identity nil)]
       (log/info :logout!/merging {:merged merged})
       (let [updated-response (assoc resp :session merged)]
         (log/info :logout!/updated {:updated-response updated-response})
         updated-response)))))

(defn check-session!
  "get session from env"
  [env]
  (log/trace :check-session!/starting {})
  (if-let [session (some-> env :ring/request :session)]
    (do
      (log/finest :check-session!/existing-session {:session session})
      (if-let [identity (:identity session)]
        (if-let [current-user (q.users/read-record identity)]
          (do
            (log/finest :check-session!/has-identity {})
            (assoc session :session/current-user current-user))
          {::auth/provider :local
           ::auth/status   :not-logged-in})
        (do
          (log/info :check-session!/no-identity {})
          {::auth/provider :local
           ::auth/status   :not-logged-in})))
    (do
      (log/info :check-session!/no-session {})
      {::auth/provider :local
       ::auth/status   :not-logged-in})))

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
          (throw (ex-info "User already exists" {})))))))

(>defn do-register
  "Register user with given name and password"
  ([name password]
   [::m.users/name ::m.users/password => (s/keys)]
   (do-register name password false))
  ([name password admin?]
   [::m.users/name ::m.users/password boolean? => (s/keys)]
   (let [params #::m.users{:password password
                           :name     name
                           :role     (if admin? :account.role/admin :account.role/user)}]
     (try
       (register params)
       (catch Exception ex
         (log/error :do-register/failed {:message "Failed to register" :exception ex})
         {::error true
          :ex     (str ex)})))))
