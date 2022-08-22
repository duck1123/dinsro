(ns dinsro.ui.core.networks
  (:require
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.core.networks :as m.c.networks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.core.network-ln-nodes :as u.c.network-ln-nodes]
   [dinsro.ui.core.network-nodes :as u.c.network-nodes]
   [lambdaisland.glogi :as log]))

(def override-form false)

(declare ShowNetwork)

(defn ShowNetwork-pre-merge
  [ctx]
  (u.links/merge-pages
   ctx
   ::m.c.networks/id
   {:ui/ln-nodes u.c.network-ln-nodes/SubPage
    :ui/nodes    u.c.network-nodes/SubPage}))

(defn ShowNetwork-will-enter
  [app {id :id}]
  (let [id    (new-uuid id)
        ident [::m.c.networks/id id]
        state (-> (app/current-state app) (get-in ident))]
    (log/finer :ShowNetwork-will-enter/starting {:app app :id id :ident ident})
    (dr/route-deferred
     ident
     (fn []
       (log/finer :ShowNetwork-will-enter/routing
                  {:id       id
                   :state    state
                   :controls (control/component-controls app)})
       (df/load!
        app ident ShowNetwork
        {:marker               :ui/selected-node
         :target               [:ui/selected-node]
         :post-mutation        `dr/target-ready
         :post-mutation-params {:target ident}})))))

(defsc ShowNetwork
  [_this {::m.c.networks/keys [chain name]
          :ui/keys            [nodes ln-nodes]}]
  {:ident         ::m.c.networks/id
   :query         [::m.c.networks/id
                   ::m.c.networks/name
                   {::m.c.networks/chain (comp/get-query u.links/ChainLinkForm)}
                   {:ui/nodes (comp/get-query u.c.network-nodes/SubPage)}
                   {:ui/ln-nodes (comp/get-query u.c.network-ln-nodes/SubPage)}]
   :initial-state {::m.c.networks/id    {}
                   ::m.c.networks/name  ""
                   ::m.c.networks/chain {}
                   :ui/nodes            {}
                   :ui/ln-nodes         {}}
   :route-segment ["network" :id]
   :pre-merge     ShowNetwork-pre-merge
   :will-enter    ShowNetwork-will-enter}
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} (str name))
     (dom/div {}
       (dom/span {} "Chain: ")
       (u.links/ui-chain-link chain)))
   (u.c.network-nodes/ui-sub-page nodes)
   (u.c.network-ln-nodes/ui-sub-page ln-nodes)))

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
