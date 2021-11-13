(ns dinsro.mutations.settings
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.mutations :as fm])
   [com.wsscode.pathom.connect :as pc]
   #?(:clj [dinsro.actions.authentication :as a.authentication])
   [dinsro.model.settings :as m.settings]
   [taoensso.timbre :as log]))

(comment ::pc/_)

(defsc Config
  [_this _props]
  {:ident         ::m.settings/id
   :query         [::m.settings/id
                   ::m.settings/initialized?
                   ::m.settings/loaded?]
   :initial-state {::m.settings/id           nil
                   ::m.settings/initialized? false
                   ::m.settings/loaded?      false}})

#?(:clj
   (pc/defmutation initialize!
     [_env {:user/keys [password username]}]
     {::pc/params #{:user/password}
      ::pc/output [::m.settings/id
                   ::m.settings/initialized?
                   ::m.settings/loaded?]}
     (let [user-id (a.authentication/do-register username password true)]
       (log/infof "Created admin account with identifier: %s" user-id)
       (m.settings/get-site-config)))
   :cljs
   (fm/defmutation initialize!
     [_props]
     (action [_env] true)
     (remote [env]
       (-> env (fm/returning Config)))))

#?(:clj (def resolvers [initialize!]))
