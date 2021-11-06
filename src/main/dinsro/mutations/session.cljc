(ns dinsro.mutations.session
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   #?(:clj [com.fulcrologic.fulcro.server.api-middleware :refer [augment-response]])
   #?(:cljs [com.fulcrologic.fulcro.ui-state-machines :as uism])
   [com.fulcrologic.rad.authorization :as auth]
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   #?(:clj [dinsro.model.authorization :as m.authorization])
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as log]))

(comment ::auth/_ ::m.users/_ ::pc/_ ::s/_)

(defsc CurrentUser
  [_this _props]
  {:query [:user/username :user/valid?]})

#?(:clj
   (pc/defmutation register
     [_env {:user/keys [password username]}]
     {::pc/params #{:user/password :user/username}
      ::pc/output [:user/username :user/valid? :user/registered?]}
     (a.authentication/do-register username password))
   :cljs
   (fm/defmutation register [_]
     (action [_env] (log/info "register"))
     (remote [_env] true)))

#?(:clj
   (pc/defmutation login [env params]
     {::pc/params #{:user/username :user/password}
      ::pc/output [::auth/provider
                   ::auth/status
                   {:session/current-user-ref [::m.users/id]}
                   ::m.users/name]}
     (m.authorization/login! env params))
   :cljs
   (fm/defmutation login [_]
     (action [{:keys [state]}]
       (log/info "busy"))

     (error-action [{:keys [state]}]
       (log/info "error action"))

     (ok-action [{:keys [state] :as env}]
       (let [body                   (get-in env [:result :body])
             {::auth/keys [status]} (get body `login)
             valid?                 (= status :success)]
         (if valid?
           nil
           (-> state
               (swap! #(assoc-in % [:component/id :dinsro.ui.forms.login/form :user/message]
                                 "Can't log in"))))))
     (remote [env]
       (fm/returning env auth/Session))))

#?(:clj
   (pc/defmutation logout
     [{{:keys [session]} :request} _]
     {::pc/params #{}
      ::pc/output [:user/username :user/valid?
                   ::auth/provider
                   {:session/current-user-ref [::m.users/id]}
                   ::m.users/name]}
     (augment-response
      {::auth/provider           :local
       :session/current-user-ref nil
       ::auth/status             :not-logged-in
       ::m.users/name            nil
       :user/username            nil
       :user/valid?              false}
      (fn [ring-response]
        (assoc ring-response :session (assoc session :identity nil)))))
   :cljs
   (fm/defmutation logout [_]
     (action [{:keys [state]}]
       true)

     (error-action [{:keys [state]}]
       (log/info "error action"))

     (ok-action [{:keys [state result] :as env}]
       (let [{::auth/keys [provider]}
             (get-in result [:body `logout])]
         (auth/logout! env provider)
         (log/infof "ok")))

     (remote [env]
       (fm/with-target env [:session/current-user]))))

#?(:clj
   (pc/defmutation check-session [env _]
     {}
     (m.authorization/check-session! env))
   :cljs
   (fm/defmutation check-session [_]
     (ok-action [{:keys [state app result]}]
       (let [{::auth/keys [provider]}   (get-in result [:body `check-session])
             {:time-zone/keys [zone-id]
              ::auth/keys     [status]} (some-> state deref ::auth/authorization (get provider))]
         (when (= status :success)
           (when zone-id
             (log/info "Setting UI time zone" zone-id)
             (comment (datetime/set-timezone! time-zone))))
         (uism/trigger! app auth/machine-id :event/session-checked {:provider provider})))
     (remote [env]
       (fm/returning env auth/Session))))

#?(:clj
   (def resolvers [check-session login logout register]))
