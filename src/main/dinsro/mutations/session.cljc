(ns dinsro.mutations.session
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   #?(:cljs [com.fulcrologic.fulcro.ui-state-machines :as uism])
   [com.fulcrologic.rad.authorization :as auth]
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.users :as m.users]
   [lambdaisland.glogc :as log]))

(comment ::auth/_ ::m.users/_ ::pc/_ ::s/_)

(defsc UserLink
  [_this _props]
  {:ident ::m.users/id
   :query [::m.users/id ::m.users/name]})

(defsc Session
  [_this _props]
  {:query [:com.fulcrologic.rad.authorization/provider
           :com.fulcrologic.rad.authorization/status
           :identity
           {:session/current-user (comp/get-query UserLink)}
           :time-zone/zone-id]
   :ident [::auth/authorization ::auth/provider]})

#?(:clj
   (pc/defmutation register
     [_env {:user/keys [password username]}]
     {::pc/params #{:user/password :user/username}
      ::pc/output [:user/username :user/valid? :user/registered?]}
     (a.authentication/do-register username password))
   :cljs
   (fm/defmutation register [_]
     (action [_env] (log/info :registration/starting {}))
     (remote [_env] true)))

#?(:clj
   (pc/defmutation login [env params]
     {::pc/params #{:user/username :user/password}
      ::pc/output [::auth/provider
                   ::auth/status
                   :identity
                   :time-zone/zone-id
                   {:session/current-user [::m.users/id ::m.users/name]}]}
     (let [response (a.authentication/login! env params)]
       (log/info :login/finished {:response response})
       response))
   :cljs
   (fm/defmutation login [_]
     (action [_env]
       (log/info :login/starting {:message "busy"}))

     (error-action [{:keys [app]}]
       (auth/failed! app :local))

     (ok-action [{:keys [app state] :as env}]
       (let [body                   (get-in env [:result :body])
             {::auth/keys [status] :as response} (get body `login)]
         (log/info :login-ok-action/starting {:response response})
         (if (= status :success)
           (auth/logged-in! app :local)
           (do
             (log/info :login/failed {})
             (auth/failed! app :local)
             (-> state
                 (swap! #(assoc-in % [:component/id :dinsro.ui.forms.login/form :user/message]
                                   "Can't log in")))))))
     (remote [env]
       (fm/returning env Session))))

#?(:clj
   (pc/defmutation logout
     [{{:keys [session]} :ring/request :as env} _]
     {::pc/params #{}
      ::pc/output [::auth/provider
                   ::auth/status
                   :identity
                   :time-zone/zone-id
                   {:session/current-user [::m.users/id]}]}
     (log/info :logout/begin {:session session})
     (a.authentication/logout! env))
   :cljs
   (fm/defmutation logout [_]
     (action [_env] true)

     (error-action [_env]
       (log/error :logout/error {}))

     (ok-action [{:keys [result] :as env}]
       (let [{::auth/keys [provider]}
             (get-in result [:body `logout])]
         (auth/logout! env provider)
         (log/info :logout/ok {})))

     (remote [env]
       (fm/returning env Session))))

#?(:clj
   (pc/defmutation check-session [env _]
     {::pc/output [::auth/provider
                   ::auth/status
                   :identity
                   :session/current-user
                   :time-zone/zone-id]}
     (a.authentication/check-session! env))
   :cljs
   (fm/defmutation check-session [_]
     (ok-action [{:keys [app result]}]
       (let [{::auth/keys [provider]} (get-in result [:body `check-session])]
         (uism/trigger! app auth/machine-id :event/session-checked {:provider provider})))
     (remote [env]
       (fm/returning env Session))))

#?(:clj
   (def resolvers [check-session login logout register]))
