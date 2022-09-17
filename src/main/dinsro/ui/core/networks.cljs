(ns dinsro.ui.core.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.routing :as rroute]
   [com.fulcrologic.semantic-ui.collections.menu.ui-menu :refer [ui-menu]]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.core.network-addresses :as u.c.network-addresses]
   [dinsro.ui.core.network-blocks :as u.c.network-blocks]
   [dinsro.ui.core.network-ln-nodes :as u.c.network-ln-nodes]
   [dinsro.ui.core.network-nodes :as u.c.network-nodes]
   [dinsro.ui.core.network-wallets :as u.c.network-wallets]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(def override-form false)

(defrouter Router
  [_this props]
  {:router-targets [u.c.network-addresses/SubPage
                    u.c.network-blocks/SubPage
                    u.c.network-nodes/SubPage
                    u.c.network-ln-nodes/SubPage
                    u.c.network-wallets/SubPage]}
  (dom/div :.ui.segment "Default route" (pr-str props)))

(def ui-router (comp/factory Router))

(defsc ShowNetwork
  [this {::m.c.networks/keys [id chain name]
         :ui/keys            [addresses blocks nodes ln-nodes wallets router]}]
  {:ident         ::m.c.networks/id
   :query         [::m.c.networks/id
                   ::m.c.networks/name
                   {::m.c.networks/chain (comp/get-query u.links/ChainLinkForm)}
                   {:ui/addresses (comp/get-query u.c.network-addresses/SubPage)}
                   {:ui/blocks (comp/get-query u.c.network-blocks/SubPage)}
                   {:ui/nodes (comp/get-query u.c.network-nodes/SubPage)}
                   {:ui/ln-nodes (comp/get-query u.c.network-ln-nodes/SubPage)}
                   {:ui/wallets (comp/get-query u.c.network-wallets/SubPage)}
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.c.networks/id    nil
                   ::m.c.networks/name  ""
                   ::m.c.networks/chain {}
                   :ui/addresses        {}
                   :ui/blocks           {}
                   :ui/nodes            {}
                   :ui/ln-nodes         {}
                   :ui/router           {}
                   :ui/wallets          {}}
   :route-segment ["network" :id]
   :pre-merge     (u.links/page-merger
                   ::m.c.networks/id
                   {:ui/addresses u.c.network-addresses/SubPage
                    :ui/blocks    u.c.network-blocks/SubPage
                    :ui/ln-nodes  u.c.network-ln-nodes/SubPage
                    :ui/nodes     u.c.network-nodes/SubPage
                    :ui/wallets   u.c.network-wallets/SubPage})
   :will-enter    (partial u.links/page-loader ::m.c.networks/id ::ShowNetwork)}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} (str name))
     (dom/div {}
       (dom/span {} "Chain: ")
       (u.links/ui-chain-link chain)))
   (ui-menu
    {:items [{:key   "addresses"
              :name  "Addresses"
              :route u.c.network-addresses/SubPage}
             {:key   "blocks"
              :name  "Blocks"
              :route u.c.network-blocks/SubPage}]

     :onItemClick
     (fn [_e d]
       (log/info :onItemClick/starting {:d d})
       (let [route (get (js->clj d) "route")]
         (log/info :onItemClick/starting {:route route})
         (rroute/route-to! this route {:id      (str id)
                                       :subpage "foo"})))})

   (ui-router router)
   (u.c.network-addresses/ui-sub-page addresses)
   (u.c.network-blocks/ui-sub-page blocks)
   (u.c.network-nodes/ui-sub-page nodes)
   (u.c.network-ln-nodes/ui-sub-page ln-nodes)
   (u.c.network-wallets/ui-sub-page wallets)))

(form/defsc-form CoreNetworkForm
  [this props]
  {fo/id             m.c.networks/id
   fo/attributes     [m.c.networks/name]
   fo/cancel-route   ["networks"]
   fo/route-prefix   "network"
   fo/title          "Network"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} "foo")
      (form/render-layout this props))))

(report/defsc-report CoreNetworksReport
  [_this _props]
  {ro/columns          [m.c.networks/name
                        m.c.networks/chain]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.networks/chain #(u.links/ui-chain-link %2)
                        ::m.c.networks/name  #(u.links/ui-network-link %3)}
   ro/form-links       {::m.c.networks/id CoreNetworkForm}
   ro/source-attribute ::m.c.networks/index
   ro/title            "Networks"
   ro/row-pk           m.c.networks/id
   ro/run-on-mount?    true
   ro/route            "networks"})

(def ui-tx-report (comp/factory CoreNetworksReport))
