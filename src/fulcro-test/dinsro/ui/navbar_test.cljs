(ns dinsro.ui.navbar-test
  (:require
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
                  :navlink/href "/transactions"}})

(defn navlink-idents
  [kws]
  (map
   (fn [kw]
     [:navlink/id kw])
   kws))

(ws/defcard Navbar
  {::wsm/card-height 5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.navbar/Navbar
    ::ct.fulcro3/initial-state
    (fn [] {:navlink/id navlink-table
            :auth/id 1
            :navbar/expanded? true
            :navbar/menu-links (navlink-idents [:accounts :transactions])
            :navbar/top-level-links (navlink-idents [:accounts :transactions])
            :navbar/unauthenticated-links (navlink-idents [:login :register])
            :navbar/dropdown-menu-links (navlink-idents [:foo :bar :baz])})
    ::ct.fulcro3/wrap-root? false}))
