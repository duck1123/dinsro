(ns dinsro.ui.navbars-test
  (:require
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   dinsro.machines
   [dinsro.mocks.ui.navbars :refer [nav-state menu-links unauth-links]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.navbars :as mu.navbars]
   [dinsro.specs :as ds]
   [dinsro.ui.navbars :as u.navbars]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NavLink
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.navbars/NavLink
    ::ct.fulcro3/initial-state
    (fn [] {::m.navlinks/id         "foo"
            ::m.navlinks/label      (ds/gen-key ::m.navlinks/label)
            ::m.navlinks/auth-link? false
            ::m.navlinks/target     nil
            :ui/router              {}})}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NavbarLogoutLink
  {}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root       u.navbars/NavbarLogoutLink
    ::ct.fulcro3/wrap-root? true
    ::ct.fulcro3/initial-state
    (fn [] {})}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NavbarSidebar
  {}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.navbars/NavbarLogoutLink
    ::ct.fulcro3/initial-state
    (fn [] {::m.navbars/id             :main
            ::m.navbars/dropdown-links []
            :inverted                 true})}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard Navbar
  {::wsm/align       {:flex 1}
   ::wsm/card-height 5
   ::wsm/card-width  6
   ::wsm/node-props  {:style
                      {:position  "absolute"
                       :width     "100%"
                       :transform "translate(0px, 69px)"
                       :height    "40px"}}}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root       u.navbars/Navbar
    ::ct.fulcro3/wrap-root? true
    ::ct.fulcro3/initial-state
    (fn []
      {::m.navbars/id                        :main
       :dinsro.ui.navbar/expanded?           false
       ::m.navbars/menu-links                menu-links
       ::m.navbars/unauth-links              unauth-links
       [::uism/asm-id ::mu.navbars/navbarsm] nav-state
       ::m.navlinks/id                       {}})}))
