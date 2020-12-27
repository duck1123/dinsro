(ns dinsro.ui.navbar-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.navbar :as u.navbar]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(defn navlink-idents
  [kws]
  (map
   (fn [kw]
     [:navlink/id kw])
   kws))

(defn map-links
  [links]
  (map #(comp/get-initial-state u.navbar/NavLink (sample/navlink-map %)) links))

(ws/defcard Navbar
  {::wsm/align {:flex 1}
   ::wsm/card-height 5
   ::wsm/card-width 6}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.navbar/Navbar
    ::ct.fulcro3/initial-state
    (fn [] {:navlink/id sample/navlink-map
            :auth/id 1
            :navbar/expanded? true
            :navbar/auth-data {:link {:navlink/id :user
                                      :navlink/name "User"
                                      :navlink/href "/users"}}
            :navbar/navbar-brand {}
            :navbar/menu-links (map sample/navlink-map [:foo :bar])
            :navbar/top-level-links (map sample/navlink-map [:accounts :transactions])
            :navbar/unauthenticated-links (map-links [:login :register])
            :navbar/dropdown-menu-links (map sample/navlink-map [:foo :bar :baz])})}))
