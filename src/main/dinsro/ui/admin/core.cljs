(ns dinsro.ui.admin.core
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.ui.admin.core.addresses :as u.a.c.addresses]
   [dinsro.ui.admin.core.blocks :as u.a.c.blocks]
   [dinsro.ui.admin.core.chains :as u.a.c.chains]
   [dinsro.ui.admin.core.dashboard :as u.a.c.dashboard]
   [dinsro.ui.admin.core.mnemonics :as u.a.c.mnemonics]
   [dinsro.ui.admin.core.networks :as u.a.c.networks]
   [dinsro.ui.admin.core.nodes :as u.a.c.nodes]
   [dinsro.ui.admin.core.peers :as u.a.c.peers]
   [dinsro.ui.admin.core.transactions :as u.a.c.transactions]
   [dinsro.ui.admin.core.wallets :as u.a.c.wallets]
   [dinsro.ui.admin.core.words :as u.a.c.words]
   [dinsro.ui.menus :as u.menus]
   [lambdaisland.glogc :as log]))

(def router-key :dinsro.ui.admin/Router)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.c.dashboard/Dashboard
    u.a.c.addresses/NewForm
    u.a.c.addresses/Report
    u.a.c.blocks/Show
    u.a.c.blocks/Report
    u.a.c.chains/NewForm
    u.a.c.chains/Report
    u.a.c.chains/Show
    u.a.c.mnemonics/Report
    u.a.c.networks/Report
    u.a.c.networks/Show
    u.a.c.nodes/NewForm
    u.a.c.nodes/Report
    u.a.c.nodes/Show
    u.a.c.peers/Report
    u.a.c.peers/Show
    u.a.c.peers/NewForm
    u.a.c.transactions/Report
    u.a.c.transactions/Show
    u.a.c.wallets/NewForm
    u.a.c.wallets/Show
    u.a.c.wallets/Report
    u.a.c.words/Report]})

(def ui-router (comp/factory Router))

(defsc Page
  [_this {:ui/keys [nav-menu router]}]
  {:ident         (fn [] [:component/id ::Page])
   :initial-state (fn [props]
                    (log/trace :Page/initial-state {:props props})
                    {:ui/nav-menu (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :admin-core})
                     :ui/router   (comp/get-initial-state Router)})
   :query         [{:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["core"]}
  (comp/fragment
   (u.menus/ui-nav-menu nav-menu)
   (ui-router router)))
