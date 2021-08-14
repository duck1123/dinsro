(ns dinsro.ui
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [com.fulcrologic.rad.authorization :as auth]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pushable :refer [ui-sidebar-pushable]]
   [com.fulcrologic.semantic-ui.modules.sidebar.ui-sidebar-pusher :refer [ui-sidebar-pusher]]
   [dinsro.machines :as machines]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.router :as router]
   [dinsro.ui.authenticator :as u.authenticator]
   [dinsro.ui.media :as u.media]
   [dinsro.ui.navbar :as u.navbar]
   [taoensso.timbre :as log]))

(defsc NavbarUnion
  [_this _props]
  {:query         [:navbar/id
                   {::navbar (comp/get-query u.navbar/Navbar)}
                   {::sidebar (comp/get-query u.navbar/NavbarSidebar)}]
   :initial-state {:navbar/id :main
                   ::navbar   {}
                   ::sidebar  {}}})

(defsc Root [this {:root/keys [navbar sidebar]
                   ::keys     [router]}]
  {:componentDidMount
   (fn [this]
     (uism/begin! this machines/hideable ::u.navbar/navbarsm
                  {:actor/navbar (uism/with-actor-class [:navbar/id :main] u.navbar/Navbar)})
     (df/load! this ::m.navlink/current-navbar u.navbar/Navbar
               {:target [:root/navbar]})
     (df/load! this ::m.navlink/current-navbar u.navbar/NavbarSidebar
               {:target [:root/sidebar]}))
   :query
   [{:authenticator (comp/get-query u.authenticator/Authenticator)}

    {:root/navbar (comp/get-query u.navbar/Navbar)}
    {:root/sidebar (comp/get-query u.navbar/NavbarSidebar)}
    {::router (comp/get-query router/RootRouter)}
    ::auth/authorization]
   :initial-state {:root/navbar   {}
                   :root/sidebar  {}
                   :authenticator {}
                   ::router       {}}}
  (let [inverted         true
        visible          (= (uism/get-active-state this ::u.navbar/navbarsm) :state/shown)
        top-router-state (or (uism/get-active-state this ::router/RootRouter) :initial)]
    (comp/fragment
     (u.media/ui-media-styles)
     (u.media/ui-media-context-provider
      {}
      (dom/div {:className "ui container"
                :style     {:height "100%"}}
        (when navbar
          (u.navbar/ui-navbar navbar))
        (ui-sidebar-pushable
         {:inverted (str inverted)
          :visible  (str visible)}
         (when sidebar
           (u.navbar/ui-navbar-sidebar sidebar))
         (ui-sidebar-pusher
          {}
          (if (= :initial top-router-state)
            (dom/div :.loading "Loading...")
            (router/ui-root-router router)))))))))

(def ui-root (comp/factory Root))
