(ns dinsro.ui
  (:require
   #?(:cljs [com.fulcrologic.fulcro.components :as comp :refer [defsc]])
   #?(:cljs [com.fulcrologic.fulcro.data-fetch :as df])
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:cljs [com.fulcrologic.fulcro.ui-state-machines :as uism])
   #?(:cljs [dinsro.machines :as machines])
   [dinsro.model.navlink :as m.navlink]
   [dinsro.router :as router]
   #?(:cljs [dinsro.ui.navbar :as u.navbar])
   [taoensso.timbre :as log]))

(comment ::m.navlink/_ ::router/_)

#?(:cljs
   (defsc Root [this {::keys [navbar router]}]
     {:componentDidMount
      (fn [this]
        (uism/begin! this machines/hideable ::navbarsm {:actor/navbar u.navbar/Navbar})

        (df/load! this ::m.navlink/current-navbar u.navbar/Navbar
                  {:target [::m.navlink/current-navbar]}))
      :query         [{::navbar (comp/get-query u.navbar/Navbar)}
                      {::router (comp/get-query router/RootRouter)}]
      :initial-state {::navbar {}
                      ::router {}}}
     (let [top-router-state (or (uism/get-active-state this ::router/RootRouter) :initial)]
       (dom/div {}
         (u.navbar/ui-navbar navbar)
         (dom/div :.ui.container
           (if (= :initial top-router-state)
             (dom/div :.loading "Loading...")
             (router/ui-root-router router)))))))
