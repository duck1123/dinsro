(ns dinsro.ui.navbar-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.model.navbar :as m.navbar]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.sample :as sample]
   [dinsro.specs :as ds]
   dinsro.machines
   [dinsro.ui.navbar :as u.navbar]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn navlink-idents
  [kws]
  (map (partial vector ::m.navlink/id)
       kws))

(defn map-links
  [links]
  (map #(comp/get-initial-state u.navbar/NavLink (sample/navlink-map %)) links))

(ws/defcard NavLink
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.navbar/NavLink
    ::ct.fulcro3/initial-state
    (fn [] {::m.navlink/id         "foo"
            ::m.navlink/name       (ds/gen-key ::m.navlink/name)
            ::m.navlink/auth-link? false
            ::m.navlink/target     nil
            :root/router           {}})}))

(def unauth-links [])

(def menu-links [])

(def nav-state
  {:com.fulcrologic.fulcro.ui-state-machines/asm-id           :dinsro.mutations.navbar/navbarsm,
   :com.fulcrologic.fulcro.ui-state-machines/state-machine-id dinsro.machines/hideable,
   :com.fulcrologic.fulcro.ui-state-machines/active-state     :state/hidden,
   :com.fulcrologic.fulcro.ui-state-machines/ident->actor     {:actor/navbar {}}
   :com.fulcrologic.fulcro.ui-state-machines/actor->ident     {:actor/navbar {}}})

(ws/defcard Navbar
  {::wsm/align       {:flex 1}
   ::wsm/card-height 5
   ::wsm/card-width  6
   ::wsm/node-props  {:style
                      {:position  "absolute"
                       :width     "100%"
                       :transform "translate(0px, 350%)"
                       :height    "40px"}}}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.navbar/Navbar
    ::ct.fulcro3/initial-state
    (fn []
      {::m.navbar/id                                                                        :main
       :dinsro.ui.navbar/expanded?                                                          false
       ::m.navbar/menu-links                                                                menu-links
       ::m.navbar/unauth-links                                                              unauth-links
       [:com.fulcrologic.fulcro.ui-state-machines/asm-id :dinsro.mutations.navbar/navbarsm] nav-state
       ::m.navlink/id                                                                       sample/navlink-map})}))
