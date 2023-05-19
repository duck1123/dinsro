(ns dinsro.ui.core.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.core.networks :as j.c.networks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.ui.core.networks.addresses :as u.c.n.addresses]
   [dinsro.ui.core.networks.blocks :as u.c.n.blocks]
   [dinsro.ui.core.networks.ln-nodes :as u.c.n.ln-nodes]
   [dinsro.ui.core.networks.nodes :as u.c.n.nodes]
   [dinsro.ui.core.networks.wallets :as u.c.n.wallets]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.c.n.addresses/SubPage
    u.c.n.blocks/SubPage
    u.c.n.nodes/SubPage
    u.c.n.ln-nodes/SubPage
    u.c.n.wallets/SubPage]})

(defsc Show
  [_this {::m.c.networks/keys [id chain name]
          :ui/keys            [nav-menu router]
          :as                 props}]
  {:ident     ::m.c.networks/id
   :initial-state
   (fn [props]
     (let [id (::m.c.networks/id props)]
       {::m.c.networks/id    nil
        ::m.c.networks/name  ""
        ::m.c.networks/chain {}
        :ui/nav-menu         (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :core-networks :id id})
        :ui/router           (comp/get-initial-state Router)}))
   :pre-merge (u.loader/page-merger
               ::m.c.networks/id
               {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :core-networks}]
                :ui/router   [Router {}]})
   :query     [::m.c.networks/id
               ::m.c.networks/name
               {::m.c.networks/chain (comp/get-query u.links/ChainLinkForm)}
               {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
               {:ui/router (comp/get-query Router)}]

   :route-segment ["network" :id]
   :will-enter    (partial u.loader/page-loader ::m.c.networks/id ::Show)}
  (if id
    (comp/fragment
     (dom/div :.ui.segment
       (dom/dl {}
         (dom/dt {} "Name")
         (dom/dd {} (str name))
         (dom/dt {} "Chain")
         (dom/dd {} (if chain (u.links/ui-chain-link chain) "None"))))
     (u.menus/ui-nav-menu nav-menu)
     ((comp/factory Router) router))
    (dom/div :.ui.segment
      (dom/h3 {} "Network Not loaded")
      (u.debug/ui-props-logger props))))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.c.networks/chain #(u.links/ui-chain-link %2)
                         ::m.c.networks/name  #(u.links/ui-network-link %3)}
   ro/columns           [m.c.networks/name
                         m.c.networks/chain]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "networks"
   ro/row-pk            m.c.networks/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.c.networks/index
   ro/title             "Networks"})
