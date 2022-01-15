(ns dinsro.model.authentication
  (:require
   [clojure.spec.alpha :as s]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   #?(:clj [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]])
   #?(:cljs [com.fulcrologic.fulcro.ui-state-machines :as uism])
   #?(:clj [com.fulcrologic.guardrails.core :refer [>defn =>]])
   [com.fulcrologic.rad.authorization :as auth]
   #?(:cljs [com.fulcrologic.rad.type-support.date-time :as datetime])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   #?(:clj [dinsro.model.authorization :as exauth])
   [dinsro.model.users :as m.users]
   #?(:clj [dinsro.queries.users :as q.users])
   [taoensso.timbre :as log]))

(comment ::auth/_ ::m.users/_ ::pc/_ ::s/_)

#?(:clj
   (>defn do-register
     [name password]
     [::m.users/name ::m.users/password => (s/keys)]
     (let [params #::m.users{:password password :name name}]
       (try
         (a.authentication/register params)
         (catch Exception ex
           ;; (log/error ex "error")
           {::error true
            :ex     (str ex)})))))

#?(:clj
   (pc/defmutation register
     [_env {:user/keys [password username]}]
     {::pc/params #{:user/password :user/username}
      ::pc/output [:user/username :user/valid? :user/registered?]}
     (log/info "register")
     (do-register username password))
   :cljs
   (fm/defmutation register [_]
     (action [_env] (log/info "register"))
     (remote [_env] true)))

(s/def ::login-response (s/keys))

#?(:clj
   (>defn do-login
     [session username password]
     [any? ::m.users/name ::m.users/password => ::login-response]
     (if-let [_user (q.users/find-eid-by-name username)]
       (if (= password m.users/default-password)
         (let [response {:user/username username
                         :user/valid?   true}
               handler  (fn [ring-response]
                          (assoc ring-response :session (assoc session :identity username)))]
           (augment-response response handler))
         {:user/username nil
          :user/valid?   false})
       {:user/username nil
        :user/valid?   false})))

#?(:clj
   (pc/defmutation login [env params]
     {::pc/params #{:user/username :user/password}}
     (exauth/login! env params))
   :cljs
   (fm/defmutation login [_params]
     (ok-action [{:keys [app state] :as props}]
       (log/spy :info props)
       (let [{:time-zone/keys [zone-id]
              ::auth/keys     [status]} (some-> state deref ::auth/authorization :local)]
         (if (= status :success)
           (do
             (when zone-id
               (log/info "Setting UI time zone" zone-id)
               (datetime/set-timezone! zone-id))
             (log/info "logged in")
             (auth/logged-in! app :local))
           (auth/failed! app :local))))
     (error-action [{:keys [app]}]
       (log/error "Login failed.")
       (auth/failed! app :local))
     (remote [env]
       (fm/returning env auth/Session))))

#?(:clj
   (pc/defmutation logout
     [{{:keys [session]} :request} _]
     {::pc/params #{}
      ::pc/output [:user/username :user/valid?]}
     (augment-response
      {:user/username nil
       :user/valid?   false}
      (fn [ring-response]
        (assoc ring-response :session (assoc session :identity nil)))))
   :cljs
   (fm/defmutation logout [_]
     (action [_env]
       (log/info "busy"))

     (error-action [_env]
       (log/info "error action"))

     (ok-action [_env]
       (log/infof "ok"))

     (remote [env]
       (fm/with-target env [:session/current-user]))))

#?(:clj
   (pc/defmutation check-session [env _]
     {}
     (exauth/check-session! env))
   :cljs
   (fm/defmutation check-session [_]
     (ok-action [{:keys [state app result]}]
       (log/info "check session ok")
       (let [{::auth/keys [provider]}   (get-in result [:body `check-session])
             {:time-zone/keys [zone-id]
              ::auth/keys     [status]} (some-> state deref ::auth/authorization (get provider))]
         (when (= status :success)
           (when zone-id
             (log/info "Setting UI time zone" zone-id)
             #_(datetime/set-timezone! time-zone)))
         (uism/trigger! app auth/machine-id :event/session-checked {:provider provider})))
     (remote [env]
       (log/info "check session remote")
       (fm/returning env auth/Session))))

#?(:clj
   (def resolvers [check-session login logout register]))
