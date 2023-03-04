(ns dinsro.ui.core.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.networks :as j.c.networks]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.core.networks.addresses :as u.c.n.addresses]
   [dinsro.ui.core.networks.blocks :as u.c.n.blocks]
   [dinsro.ui.core.networks.ln-nodes :as u.c.n.ln-nodes]
   [dinsro.ui.core.networks.nodes :as u.c.n.nodes]
   [dinsro.ui.core.networks.wallets :as u.c.n.wallets]
   [dinsro.ui.links :as u.links]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.c.n.addresses/SubPage
    u.c.n.blocks/SubPage
    u.c.n.nodes/SubPage
    u.c.n.ln-nodes/SubPage
    u.c.n.wallets/SubPage]})

(def menu-items
  [{:key   "addresses"
    :name  "Addresses"
    :route "dinsro.ui.core.networks.addresses/SubPage"}
   {:key   "blocks"
    :name  "Blocks"
    :route "dinsro.ui.core.networks.blocks/SubPage"}
   {:name  "LN Nodes"
    :key   "ln-nodes"
    :route "dinsro.ui.core.networks.ln-nodes/SubPage"}
   {:name  "Core Nodes"
    :key   "core-nodes"
    :route "dinsro.ui.core.networks.nodes/SubPage"}
   {:name  "Wallets"
    :key   "wallets"
    :route "dinsro.ui.core.networks.wallets/SubPage"}])

(defsc Show
  [_this {::m.c.networks/keys [id chain name]
          :ui/keys            [router]
          :as                 props}]
  {:ident         ::m.c.networks/id
   :query         [::m.c.networks/id
                   ::m.c.networks/name
                   {::m.c.networks/chain (comp/get-query u.links/ChainLinkForm)}
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.c.networks/id    nil
                   ::m.c.networks/name  ""
                   ::m.c.networks/chain {}
                   :ui/router           {}}
   :route-segment ["network" :id]
   :pre-merge     (u.links/page-merger ::m.c.networks/id {:ui/router Router})
   :will-enter    (partial u.links/page-loader ::m.c.networks/id ::Show)}
  (if id
    (comp/fragment
     (dom/div :.ui.segment
       (dom/dl {}
         (dom/dt {} "Name")
         (dom/dd {} (str name))
         (dom/dt {} "Chain")
         (dom/dd {} (if chain (u.links/ui-chain-link chain) "None"))))
     (u.links/ui-nav-menu {:id id :menu-items menu-items})
     ((comp/factory Router) router))
    (dom/div :.ui.segment
      (dom/h3 {} "Network Not loaded")
      (u.links/ui-props-logger props))))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.c.networks/name
                        m.c.networks/chain]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.networks/chain #(u.links/ui-chain-link %2)
                        ::m.c.networks/name  #(u.links/ui-network-link %3)}
   ro/source-attribute ::j.c.networks/index
   ro/title            "Networks"
   ro/row-pk           m.c.networks/id
   ro/run-on-mount?    true
   ro/route            "networks"})
