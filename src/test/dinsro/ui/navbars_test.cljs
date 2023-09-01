(ns dinsro.ui.navbars-test
  (:require
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   dinsro.machines
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

(def unauth-links
  [{::m.navlinks/id         :foo
    ::m.navlinks/label      (ds/gen-key ::m.navlinks/label)
    ::m.navlinks/auth-link? false
    ::m.navlinks/target     nil
    :ui/router              {}}])

(def menu-links
  [])

(def nav-state
  {::uism/asm-id                                          :dinsro.mutations.navbar/navbarsm,
   ::uism/state-machine-id                                dinsro.machines/hideable,
   ::uism/active-state                                    :state/hidden,
   :com.fulcrologic.fulcro.ui-state-machines/ident->actor {:actor/navbar {}}
   :com.fulcrologic.fulcro.ui-state-machines/actor->ident {:actor/navbar {}}})

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
