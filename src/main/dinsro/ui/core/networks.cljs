(ns dinsro.ui.core.networks
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   ;; [com.fulcrologic.rad.form :as form]
   ;; [com.fulcrologic.rad.form-options :as fo]
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
  [this props]
  {:router-targets [u.c.network-addresses/SubPage
                    u.c.network-blocks/SubPage
                    u.c.network-nodes/SubPage
                    u.c.network-ln-nodes/SubPage
                    u.c.network-wallets/SubPage]}
  (log/info :Router/starting {:props props :this this})
  (let [{:keys [current-state pending-path-segment route-props
                #_route-factory]} props]
    (case current-state
      (dom/div :.ui.segment
        (dom/div {} "Default route")
        (dom/p {} "Current State: " (pr-str current-state))
        (dom/p {} "Segment: " (pr-str pending-path-segment))
        (dom/p {} "Props: " (pr-str route-props))
        (dom/p {} "Props 2: " (pr-str props))
        (dom/code {} (pr-str route-props))))))

(def ui-router (comp/factory Router))

(defsc ShowNetwork
  [this {::m.c.networks/keys [id chain name]
         :ui/keys            [router]
         :as                 props}]
  {:ident         ::m.c.networks/id
   :query         [::m.c.networks/id
                   ::m.c.networks/name
                   {::m.c.networks/chain (comp/get-query u.links/ChainLink)}
                   {:ui/router (comp/get-query Router)}]
   :initial-state {::m.c.networks/id    nil
                   ::m.c.networks/name  ""
                   ::m.c.networks/chain {}
                   :ui/router           {}}
   :route-segment ["network" :id]

  ;;  :pre-merge
  ;;  (fn [ctx]
  ;;    (log/info :ShowNetwork/pre-merge-starting {:ctx ctx})
  ;;    (let [{:keys [data-tree state-map]} ctx
  ;;          id                            (::m.c.networks/id data-tree)
  ;;          merged-data-tree              (merge
  ;;                                         (comp/get-initial-state ShowNetwork)
  ;;                                         {:ui/router (-> state-map
  ;;                                                         (get-in (comp/get-ident Router {}))
  ;;                                                         (assoc ::m.c.networks/id id))}
  ;;                                         data-tree)]
  ;;      (log/info :ShowNetwork/pre-merge-finished {:merged-data-tree merged-data-tree})
  ;;      merged-data-tree))

   :will-enter    (partial u.links/page-loader ::m.c.networks/id ::ShowNetwork)}
  (log/info :ShowNetwork/starting {:props props})
  (if id
    (comp/fragment
     (dom/div :.ui.segment
       (dom/dl {}
         (dom/dt {} "Name")
         (dom/dd {} (str name))
         (dom/dt {} "Chain: ")
         (dom/dd {}
           (if chain
             (u.links/ui-chain-link2 chain)
             "None")))
       ;; (dom/h1 {} (str name))
       ;; (dom/div {}
       ;;   (dom/span {} "Chain: ")
       ;;   (if chain
       ;;     (u.links/ui-chain-link2 chain)
       ;;     "None"))
       #_(u.links/log-props props))
     (ui-menu
      {:items [{:key   "addresses"
                :name  "Addresses"
                :route "dinsro.ui.core.network-addresses/SubPage"}
               {:key   "blocks"
                :name  "Blocks"
                :route "dinsro.ui.core.network-blocks/SubPage"}
               {:name  "LN Nodes"
                :key   "ln-nodes"
                :route "dinsro.ui.core.network-ln-nodes/SubPage"}
               {:name  "Core Nodes"
                :key   "core-nodes"
                :route "dinsro.ui.core.network-nodes/SubPage"}
               {:name  "Wallets"
                :key   "wallets"
                :route "dinsro.ui.core.network-wallets/SubPage"}]
       :onItemClick
       (fn [_e d]
         (let [route-name (get (js->clj d) "route")
               route-kw   (keyword route-name)
               route      (comp/registry-key->class route-kw)]
           (log/info :onItemClick/kw {:route-kw route-kw :route route :id id})
           (if id
             (rroute/route-to! this route {:id (str id)})
             (log/info :onItemClick/no-id {}))))})
     (if router
       (ui-router router)
       (dom/div :.ui.segment "Network Router not loaded")))

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
