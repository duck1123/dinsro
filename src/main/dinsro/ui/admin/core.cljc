(ns dinsro.ui.admin.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.admin.core.addresses :as u.a.c.addresses]
   [dinsro.ui.admin.core.blocks :as u.a.c.blocks]
   [dinsro.ui.admin.core.chains :as u.a.c.chains]
   [dinsro.ui.admin.core.dashboard :as u.a.c.dashboard]
   [dinsro.ui.admin.core.mnemonics :as u.a.c.mnemonics]
   [dinsro.ui.admin.core.networks :as u.a.c.networks]
   [dinsro.ui.admin.core.nodes :as u.a.c.nodes]
   [dinsro.ui.admin.core.peers :as u.a.c.peers]
   [dinsro.ui.admin.core.transactions :as u.a.c.transactions]
   [dinsro.ui.admin.core.wallet-addresses :as u.a.c.wallet-addresses]
   [dinsro.ui.admin.core.wallets :as u.a.c.wallets]
   [dinsro.ui.admin.core.words :as u.a.c.words]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

(def menu-key :admin-core)
(def router-key :dinsro.ui.admin/Router)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.c.dashboard/Page
    u.a.c.addresses/IndexPage
    u.a.c.addresses/NewForm
    u.a.c.addresses/ShowPage
    u.a.c.blocks/IndexPage
    u.a.c.blocks/ShowPage
    u.a.c.chains/IndexPage
    u.a.c.chains/NewForm
    u.a.c.chains/ShowPage
    u.a.c.mnemonics/IndexPage
    u.a.c.mnemonics/ShowPage
    u.a.c.networks/IndexPage
    u.a.c.networks/ShowPage
    u.a.c.nodes/IndexPage
    u.a.c.nodes/NewForm
    u.a.c.nodes/ShowPage
    u.a.c.peers/IndexPage
    u.a.c.peers/NewForm
    u.a.c.peers/ShowPage
    u.a.c.transactions/IndexPage
    u.a.c.transactions/ShowPage
    u.a.c.wallet-addresses/IndexPage
    u.a.c.wallets/IndexPage
    u.a.c.wallets/NewForm
    u.a.c.wallets/ShowPage
    u.a.c.words/IndexPage
    u.a.c.words/ShowPage]})

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [nav-menu router]
          :as      props}]
  {:ident         (fn [] [::m.navlinks/id menu-key])
   :initial-state (fn [_props]
                    {::m.navbars/id menu-key
                     :ui/nav-menu   (comp/get-initial-state u.menus/NavMenu
                                      {::m.navbars/id menu-key})
                     :ui/router     (comp/get-initial-state Router)})
   :query         [::m.navlinks/id
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  (log/debug :Page/starting {:props props})
  (dom/div {}
    (if nav-menu
      (u.menus/ui-nav-menu nav-menu)
      (u.debug/load-error props "admin core page menu"))
    (if router
      (ui-router router)
      (u.debug/load-error props "admin core page router"))))

(m.navlinks/defroute :admin-core
  {::m.navlinks/control       ::Page
   ::m.navlinks/description   "Router page for core admin"
   ::m.navlinks/label         "Core"
   ::m.navlinks/navigate-key  :admin-core-dashboard
   ::m.navlinks/parent-key    :admin
   ::m.navlinks/router        :admin
   ::m.navlinks/required-role :admin})
