(ns dinsro.ui.core.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
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
  (let [{:keys [current-state]} props]
    (case current-state
      (dom/div {} "Unknown state"))
    (dom/div :.ui.segment "Default route" (pr-str props))))

(def ui-router (comp/factory Router))

(defsc ShowNetwork
  [this {::m.c.networks/keys [id chain name]
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
   :pre-merge
   (fn [ctx]
     (log/info :pre-merge/starting {:ctx ctx})
     (let [{:keys [data-tree]} ctx]
       (log/info :pre-merge/a {:data-tree data-tree})
       (let [new-context      {:ui/router (comp/get-initial-state Router)}
             merged-data-tree (merge data-tree new-context)]
         (log/info :pre-merge/b {:merged-data-tree merged-data-tree})
         merged-data-tree)))
   :will-enter    (partial u.links/page-loader ::m.c.networks/id ::ShowNetwork)}
  (log/info :ShowNetwork/starting {:props props})
  (if id
    (comp/fragment
     (dom/div :.ui.segment
       (dom/h1 {} (str name))
       (dom/div {}
         (dom/span {} "Chain: ")
         (if chain
           (u.links/ui-chain-link chain)
           "None")))
     (ui-menu
      {:items [{:key   "addresses"
                :name  "Addresses"
                :route "dinsro.ui.core.network-addresses/SubPage"}
               {:key   "blocks"
                :name  "Blocks"
                :route "dinsro.ui.core.network-blocks/SubPage"}]
       :onItemClick
       (fn [_e d]
         (log/info :onItemClick/starting {:d d})
         (let [route-name (get (js->clj d) "route")]
           (log/info :onItemClick/starting {:route-name route-name})
           (let [route-kw (keyword route-name)]
             (log/info :onItemClick/kw {:route-kw route-kw})
             (let [route (comp/registry-key->class route-kw)]
               (rroute/route-to! this route {:id               (str id)
                                             ::m.c.networks/id id})))))})
     (if router
       (ui-router router)
       (dom/div :.ui.segment "Router not loaded")))
    (dom/p :.ui.segment
      "Network Not loaded"
      (pr-str props))))

(report/defsc-report CoreNetworksReport
  [_this _props]
  {ro/columns          [m.c.networks/name
                        m.c.networks/chain]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.c.networks/chain #(u.links/ui-chain-link %2)
                        ::m.c.networks/name  #(u.links/ui-network-link %3)}
   ro/source-attribute ::m.c.networks/index
   ro/title            "Networks"
   ro/row-pk           m.c.networks/id
   ro/run-on-mount?    true
   ro/route            "networks"})

(def ui-tx-report (comp/factory CoreNetworksReport))
