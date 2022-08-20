(ns dinsro.ui.core.tx
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
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core.tx :as j.c.tx]
   [dinsro.model.core.blocks :as m.c.blocks]
   [dinsro.model.core.tx :as m.c.tx]
   [dinsro.model.core.tx-in :as m.c.tx-in]
   [dinsro.model.core.tx-out :as m.c.tx-out]
   [dinsro.mutations.core.tx :as mu.c.tx]
   [dinsro.ui.core.transaction-inputs :as u.c.transaction-inputs]
   [dinsro.ui.core.transaction-outputs :as u.c.transaction-outputs]
   [dinsro.ui.core.tx-out :as u.c.tx-out]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defsc RefRow
  [this {::m.c.tx/keys [fetched? id]
         :as           props}]
  {:ident         ::m.c.tx/id
   :query         [::m.c.tx/id
                   ::m.c.tx/fetched?]
   :initial-state {::m.c.tx/id       nil
                   ::m.c.tx/fetched? false}}

  (dom/tr {}
    (dom/td {} (u.links/ui-core-tx-link props))
    (dom/td {} (str fetched?))
    (dom/td {} (dom/button {:classes [:.ui.button]
                            :onClick (fn [event]
                                       (log/info :fetch-button/clicked {:event event})
                                       (comp/transact! this [(mu.c.tx/fetch! {::m.c.tx/id id})]))}
                 "Fetch"))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.c.tx/id}))

(defn ref-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "txid")
         (dom/th {} "fetched")
         (dom/th {} "Actions")))
     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-row (render-field-factory ref-row))

(form/defsc-form CoreTxInSubForm
  [_this _props]
  {fo/id           m.c.tx-in/id
   fo/route-prefix "ln-nodes-tx"
   fo/title        "Inputs"
   fo/attributes   [m.c.tx-in/txid]})

(form/defsc-form CoreTxOutSubForm
  [_this _props]
  {fo/id           m.c.tx-out/id
   fo/route-prefix "node-tx-out"
   fo/title        "Outputs"
   fo/attributes   [m.c.tx-out/value
                    m.c.tx-out/n
                    m.c.tx-out/asm
                    m.c.tx-out/hex
                    m.c.tx-out/type]})

(form/defsc-form CoreTxSubForm
  [_this _props]
  {fo/id           m.c.tx/id
   fo/route-prefix "tx-sub"
   fo/title        "Core Transaction"
   fo/attributes   [m.c.tx/fetched?
                    m.c.tx/tx-id
                    m.c.tx/block
                    j.c.tx/ins
                    j.c.tx/outs]
   fo/field-styles {::m.c.tx/block :link}
   fo/subforms     {::m.c.tx/ins  {fo/ui CoreTxInSubForm}
                    ::m.c.tx/outs {fo/ui CoreTxOutSubForm}}})

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [this _key]
             (let [{::m.c.tx/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.c.tx/fetch! {::m.c.tx/id id})])))})

(def override-input true)

(form/defsc-form CoreTxInput
  [this props]
  {fo/id           m.c.tx-in/id
   fo/route-prefix "tx-in"
   fo/attributes   [m.c.tx-in/coinbase
                    m.c.tx-in/txinwitness
                    m.c.tx-in/sequence
                    m.c.tx-in/txid]
   fo/title        "Input"}
  (if override-input
    (form/render-layout this props)
    (let [{::m.c.tx-in/keys [sequence txid vout script-pub-key]} props]
      (dom/div {}
        (when sequence
          (dom/p "sequence " sequence))
        (when txid
          (dom/p {} "txid: " txid))
        (when vout
          (dom/p {} "vout" vout))
        (when script-pub-key
          (dom/p {} "pub " script-pub-key))))))

(form/defsc-form CoreTxBlock
  [_this _props]
  {fo/id           m.c.blocks/id
   fo/route-prefix "tx-block"
   fo/attributes   [m.c.blocks/height m.c.blocks/hash]
   fo/title        "Block"})

(def override-form false)

(form/defsc-form CoreTxForm
  [this props]
  {fo/id             m.c.tx/id
   fo/action-buttons (concat [::fetch] form/standard-action-buttons)
   fo/attributes     [m.c.tx/tx-id
                      m.c.tx/fetched?
                      m.c.tx/block
                      m.c.tx/hash
                      m.c.tx/hex
                      m.c.tx/lock-time
                      m.c.tx/size
                      m.c.tx/time
                      m.c.tx/version
                      j.c.tx/ins
                      j.c.tx/outs
                      j.c.tx/node]
   fo/cancel-route   ["transactions"]
   fo/controls       (merge form/standard-controls {::fetch fetch-button})
   fo/field-styles   {::m.c.tx/block :link
                      ::m.c.tx/outs  :tx-out-table
                      ::m.c.tx/ins   :tx-in-table}
   fo/route-prefix   "tx-form"
   fo/subforms       {::m.c.tx/block {fo/ui CoreTxBlock}
                      ::m.c.tx/ins   {fo/ui CoreTxInput}
                      ::m.c.tx/outs  {fo/ui u.c.tx-out/CoreTxOutput}
                      ::m.c.tx/node  {fo/ui u.links/CoreNodeLinkForm}}
   fo/title          "Core Transaction"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} "foo")
      (form/render-layout this props))))

(defn fetch-action
  [report-instance {::m.c.tx/keys [id]}]
  (comp/transact! report-instance [(mu.c.tx/fetch! {::m.c.tx/id id})]))

(defn delete-action
  [report-instance {::m.c.tx/keys [id]}]
  (form/delete! report-instance ::m.c.tx/id id))

(def fetch-action-button
  {:label     "Fetch"
   :action    fetch-action
   :disabled? (fn [_ row-props] (:account/active? row-props))})

(def delete-action-button
  {:label  "Delete"
   :action delete-action
   :style  :delete-button})

(defn search-control-action
  [this]
  (let [props                              (comp/props this)
        {:ui/keys [controls current-rows]} props
        [current-row]                      current-rows
        values                             (map (fn [control]
                                                  (let [control-id (::control/id control)]
                                                    (log/debug :mapping {:control-id control-id})
                                                    (when (= control-id ::tx-id)
                                                      (::control/value control))))
                                                controls)
        txid-value                         (first (filter identity values))
        block-id                           nil]
    (log/info :tx/searching {:props       props
                             :current-row current-row
                             :txid-value  txid-value
                             :values      values})
    (comp/transact! this
                    [(mu.c.tx/search!
                      {::m.c.tx/block block-id
                       ::m.c.tx/tx-id txid-value})])
    (control/run! this)))

(def search-control
  {:type   :button
   :label  "Search"
   :action search-control-action})

(defn ShowTransaction-pre-merge
  [{:keys [data-tree state-map]}]
  (log/finer :ShowTransaction-pre-merge/starting
             {:data-tree data-tree
              :state-map state-map})
  (let [id (::m.c.tx/id data-tree)]
    (log/finer :ShowTransaction-pre-merge/parsed {:id id})
    (let [inputs-data
          (let [initial (comp/get-initial-state u.c.transaction-inputs/SubPage)
                state   (get-in state-map (comp/get-ident u.c.transaction-inputs/SubPage {}))
                merged  (merge initial state {::m.c.blocks/id id})]
            (log/finer :ShowTransaction-pre-merge/transaction-data {:initial initial :state state :merged merged})
            merged)

          outputs-data
          (let [initial (comp/get-initial-state u.c.transaction-outputs/SubPage)
                state   (get-in state-map (comp/get-ident u.c.transaction-outputs/SubPage {}))
                merged  (merge initial state {::m.c.blocks/id id})]
            (log/finer :ShowTransaction-pre-merge/transaction-data {:initial initial :state state :merged merged})
            merged)

          updated-data (-> data-tree
                           (assoc :ui/inputs inputs-data)
                           (assoc :ui/outputs outputs-data))]
      (log/finer :ShowBlock-pre-merge/merged
                 {:updated-data updated-data
                  :data-tree    data-tree
                  :state-map    state-map})
      updated-data)))

(defsc ShowTransaction
  "Show a core tx"
  [this {::m.c.tx/keys [id tx-id hash fetched? block size]
         :ui/keys      [inputs outputs]
         :as           props}]
  {:route-segment ["tx" :id]
   :query         [::m.c.tx/id
                   ::m.c.tx/tx-id
                   ::m.c.tx/hash
                   ::m.c.tx/size
                   ::m.c.tx/fetched?
                   {:ui/inputs (comp/get-query u.c.transaction-inputs/SubPage)}
                   {:ui/outputs (comp/get-query u.c.transaction-outputs/SubPage)}
                   {::m.c.tx/block (comp/get-query u.links/BlockHeightLinkForm)}
                   [df/marker-table '_]]
   :initial-state {::m.c.tx/id       nil
                   ::m.c.tx/tx-id    nil
                   ::m.c.tx/hash     ""
                   ::m.c.tx/block    {}
                   ::m.c.tx/size     0
                   :ui/inputs        {}
                   :ui/outputs       {}
                   ::m.c.tx/fetched? false}
   :ident         ::m.c.tx/id
   :will-enter
   (fn [app {id :id}]
     (let [id    (new-uuid id)
           ident [::m.c.tx/id id]
           state (-> (app/current-state app) (get-in ident))]
       (log/finer :ShowTransaction/will-enter {:id id :app app})
       (dr/route-deferred
        ident
        (fn []
          (log/finer :ShowTransaction/will-enter2 {:id id :state state})
          (df/load!
           app ident ShowTransaction
           {:marker               :ui/selected-node
            :target               [:ui/selected-node]
            :post-mutation        `dr/target-ready
            :post-mutation-params {:target ident}})))))
   :pre-merge     ShowTransaction-pre-merge}
  (log/finer :ShowTransaction/creating {:id id :props props :this this})
  (dom/div {}
    (dom/div :.ui.segment
      (dom/h1 {} "Transaction")
      (dom/p :.ui.segment "TX id: " (str tx-id))
      (dom/p :.ui.segment "Hash: " (str hash))
      (dom/p :.ui.segment "Block: " (u.links/ui-block-height-link block))
      (dom/p :.ui.segment
        "Fetched: "
        (dom/a {:onClick #(comp/transact! this [(mu.c.tx/fetch! {::m.c.tx/id id})])
                :href    "#"}
          (str fetched?)))
      (dom/p :.ui.segment "Size: " (str size)))
    (if id
      (comp/fragment
       (when inputs (u.c.transaction-inputs/ui-transaction-inputs-sub-page inputs))
       (when outputs (u.c.transaction-outputs/ui-transaction-outputs-sub-page outputs)))
      (dom/p {} "id not set"))))

(report/defsc-report CoreTxReport
  [_this _props]
  {ro/columns          [m.c.tx/tx-id
                        j.c.tx/node
                        m.c.tx/fetched?
                        m.c.tx/block]
   ro/controls         {::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/source-attribute ::m.c.tx/index
   ro/title            "Transactions"
   ro/row-actions      [fetch-action-button delete-action-button]
   ro/row-pk           m.c.tx/id
   ro/run-on-mount?    true
   ro/route            "transactions"})

(def ui-tx-report (comp/factory CoreTxReport))
