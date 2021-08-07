(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [dinsro.machines :as machines]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.router :as router]
   [dinsro.ui.media :as u.media]
   [dinsro.ui.navbar :as u.navbar]
   [dinsro.views.login :as v.login]
   [taoensso.timbre :as log]))

(auth/defauthenticator Authenticator {:local v.login/LoginPage})

(def ui-authenticator (comp/factory Authenticator))

(defsc Root [this {::keys [navbar router]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable ::navbarsm {:actor/navbar u.navbar/Navbar})

     (df/load! this ::m.navlink/current-navbar u.navbar/Navbar
               {:target [::m.navlink/current-navbar]}))
   :query         [{:authenticator (comp/get-query Authenticator)}
                   {::navbar (comp/get-query u.navbar/Navbar)}
                   {::router (comp/get-query router/RootRouter)}
                   ::auth/authorization]
   :initial-state {:authenticator {}
                   ::navbar       {}
                   ::router       {}}}
  (let [top-router-state (or (uism/get-active-state this ::router/RootRouter) :initial)]
    (comp/fragment
     (u.media/ui-media-styles)
     (u.media/ui-media-context-provider
      {}
      (u.navbar/ui-navbar navbar)
      (dom/div :.ui.container
        (if (= :initial top-router-state)
          (dom/div :.loading "Loading...")
          (router/ui-root-router router)))))))

(def ui-root (comp/factory Root))
