(ns dinsro.ui.core.chains
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
   [dinsro.model.core.chains :as m.c.chains]
   [dinsro.ui.core.chain-networks :as u.c.chain-networks]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogi :as log]))

(def override-form false)

(form/defsc-form CoreChainForm
  [this props]
  {fo/id             m.c.chains/id
   fo/attributes     [m.c.chains/name]
   fo/cancel-route   ["chains"]
   fo/route-prefix   "chain"
   fo/title          "Chain"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} "foo")
      (form/render-layout this props))))

(declare ShowChain)

(defn ShowChain-will-enter
  [app {id-str :id}]
  (let [id    (new-uuid id-str)
        ident [::m.c.chains/id id]
        state (-> (app/current-state app) (get-in ident))]
    (log/finer :ShowChain-will-enter/starting {:app app :id id :ident ident})
    (dr/route-deferred
     ident
     (fn []
       (log/finer :ShowChain-will-enter/routing
                  {:id       id
                   :state    state
                   :controls (control/component-controls app)})
       (df/load!
        app ident ShowChain
        {:marker               :ui/selected-node
         :target               [:ui/selected-node]
         :post-mutation        `dr/target-ready
         :post-mutation-params {:target ident}})))))

(defn ShowChain-pre-merge
  [{:keys [data-tree state-map]}]
  (let [chain-id (::m.c.chains/id data-tree)]
    (log/info :ShowChain-pre-merge/starting {:chain-id chain-id})
    (let [networks-data (u.links/merge-state state-map u.c.chain-networks/SubPage {::m.c.chains/id chain-id})
          updated-data  (-> data-tree
                            (assoc :ui/networks networks-data))]
      (log/info :ShowChain-pre-merge/finished {:updated-data updated-data})
      updated-data)))

(defsc ShowChain
  [_this {::m.c.chains/keys [name]
          :ui/keys          [networks]
          :as               props}]
  {:route-segment ["chain" :id]
   :query         [::m.c.chains/id
                   ::m.c.chains/name
                   {:ui/networks (comp/get-query u.c.chain-networks/SubPage)}
                   [df/marker-table '_]]
   :initial-state {::m.c.chains/id    nil
                   ::m.c.chains/name  ""
                   :ui/networks {}}
   :ident         ::m.c.chains/id
   :pre-merge     ShowChain-pre-merge
   :will-enter    ShowChain-will-enter}
  (log/info :ShowChain/starting {:props props})
  (comp/fragment
   (dom/div :.ui.segment
     (dom/h1 {} (str "Show Chain: " name)))
   (if networks
     (u.c.chain-networks/ui-sub-page networks)
     (dom/p "Failed to load chain networks"))))

(def ui-show-chain (comp/factory ShowChain))

(report/defsc-report Report
  [_this _props]
  {ro/columns           [m.c.chains/name]
   ro/column-formatters {::m.c.chains/name #(u.links/ui-chain-link %3)}
   ro/controls          {::refresh u.links/refresh-control}
   ro/control-layout    {:action-buttons [::refresh]}
   ro/source-attribute  ::m.c.chains/index
   ro/title             "Chains"
   ro/row-pk            m.c.chains/id
   ro/run-on-mount?     true
   ro/route             "chains"})

(def ui-tx-report (comp/factory Report))
