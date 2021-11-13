(ns dinsro.model.authorization
  (:require
   [com.fulcrologic.fulcro.server.api-middleware :as fmw]
   [com.fulcrologic.rad.attributes :as attr]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.components.database-queries :as queries]
   [dinsro.model.users :as m.users]
   [dinsro.model.timezone :as timezone]
   [taoensso.encore :as enc]
   [taoensso.timbre :as log]))

(defn login!
  "Implementation of login. This is database-specific and is not further generalized for the demo."
  [env {:user/keys [username password]}]
  (log/info "Attempt login for" username)

  (enc/if-let [{::m.users/keys  [id name hashed-value salt iterations]
                :time-zone/keys [zone-id]} (queries/get-login-info env username)
               current-hashed-value (attr/encrypt password salt iterations)]
    (if (= hashed-value current-hashed-value)
      (do
        (log/info "Login for" username)
        (let [s {::auth/provider           :local
                 ::auth/status             :success
                 :session/current-user-ref [::m.users/id id]
                 :time-zone/zone-id        (-> zone-id :xt/id timezone/datomic-time-zones)
                 ::m.users/name            name}]
          (fmw/augment-response
           s
           (fn [resp]
             (let [current-session (-> env :ring/request :session)]
               (assoc resp :session (merge current-session s)))))))
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
