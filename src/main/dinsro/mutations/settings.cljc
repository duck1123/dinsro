(ns dinsro.mutations.settings
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.fulcrologic.rad.authorization :as auth]
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.settings :as m.settings]
   [dinsro.model.users :as m.users]
   [taoensso.timbre :as log]))

(comment ::pc/_ ::m.users/_)

(defsc Config
  [_this _props]
  {:ident         ::m.settings/id
   :query         [::m.settings/id
                   ::m.settings/initialized?
                   ::m.settings/loaded?
                   {::m.settings/auth (comp/get-query auth/Session)}]
   :initial-state {::m.settings/id           :main
                   ::m.settings/initialized? false
                   ::m.settings/loaded?      false
                   ::m.settings/auth         {}}})

#?(:clj
   (pc/defmutation initialize!
     [env {:user/keys [password username]}]
     {::pc/params #{:user/password}
      ::pc/output [::m.settings/id
                   ::m.settings/initialized?
                   ::m.settings/loaded?]}
     (let [{::m.users/keys [id]} (a.authentication/do-register username password true)
           ;; TODO: Set default timezone or determine from system
           zone-id               {:xt/id :America-Detroit}]

       (log/infof "Created admin account with identifier: %s" id)
       (a.authentication/associate-session!
        env
        id
        zone-id
        (assoc (m.settings/get-site-config)
               ::m.settings/auth (a.authentication/get-auth-data id zone-id)))))
   :cljs
   (fm/defmutation initialize!
     [_props]
     (action [_env] true)
     (remote [env]
       (-> env (fm/returning Config)))))

#?(:clj (def resolvers [initialize!]))
