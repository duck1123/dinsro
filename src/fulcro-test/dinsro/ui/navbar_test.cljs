(ns dinsro.ui.navbar-test
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.navbar :as u.navbar]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard NavbarBurger
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.navbar/NavbarBurger
    ::ct.fulcro3/initial-state
    (fn [] {:navbar/expanded? true})
    ::ct.fulcro3/wrap-root? false}))

(def navlink-table
  {:foo {:navlink/id :foo
         :navlink/name "foo"
         :navlink/href "/foo"}
   :bar {:navlink/id :bar
         :navlink/name "bar"
         :navlink/href "/bar"}
   :baz {:navlink/id :baz
         :navlink/name "baz"
         :navlink/href "/baz"}
   :accounts {:navlink/id :accounts
              :navlink/name "Accounts"
              :navlink/href "/accounts"}
   :transactions {:navlink/id :transactions
                  :navlink/name "transactions"
                  :navlink/href "/transactions"}
   :users {:navlink/id :users
           :navlink/name "User"
           :navlink/href "/users"}})

(defn navlink-idents
  [kws]
  (map
   (fn [kw]
     [:navlink/id kw])
   kws))

(defn map-links
  [links]
  (map #(comp/get-initial-state u.navbar/NavLink (navlink-table %)) links))

(ws/defcard Navbar
  {::wsm/card-height 7
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.navbar/Navbar
    ::ct.fulcro3/initial-state
    (fn [] {:navlink/id navlink-table
            :auth/id 1
            :navbar/expanded? true
            :navbar/menu-links (map-links [:foo :bar])
            :navbar/top-level-links (map-links [:accounts :transactions])
            :navbar/unauthenticated-links (map-links [:login :register])
            :navbar/dropdown-menu-links (map-links [:foo :bar :baz])})
    ::ct.fulcro3/wrap-root? false}))
