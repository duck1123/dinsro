(ns dinsro.ui.core-tx
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.control :as control]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core-tx :as j.core-tx]
   [dinsro.model.core-block :as m.core-block]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.model.core-tx-in :as m.core-tx-in]
   [dinsro.model.core-tx-out :as m.core-tx-out]
   [dinsro.mutations.core-tx :as mu.core-tx]
   [dinsro.ui.core-block :as u.core-block]
   [dinsro.ui.core-tx-out :as u.core-tx-out]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defsc RefRow
  [this {::m.core-tx/keys [fetched? id]
         :as props}]
  {:ident ::m.core-tx/id
   :query [::m.core-tx/id
           ::m.core-tx/fetched?]
   :initial-state {::m.core-tx/id nil
                   ::m.core-tx/fetched? false}}

  (dom/tr {}
    (dom/td {} (u.links/ui-core-tx-link props))
    (dom/td {} (str fetched?))
    (dom/td {} (dom/button {:classes [:.ui.button]
                            :onClick (fn [event]
                                       (log/info :fetch-button/clicked {:event event})
                                       (comp/transact! this [(mu.core-tx/fetch! {::m.core-tx/id id})]))}
                 "Fetch"))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.core-tx/id}))

(defn ref-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "txid")
         (dom/th {} "fetched")
         (dom/th {} "Actions")
         ;; (dom/th {} "Hash")
         ;; (dom/th {} "Height")
         ))

     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-row (render-field-factory ref-row))

(form/defsc-form CoreTxInSubForm
  [_this _props]
  {fo/id           m.core-tx-in/id
   fo/route-prefix "ln-nodes-tx"
   fo/title        "Inputs"
   fo/attributes   [m.core-tx-in/txid]})

(form/defsc-form CoreTxOutSubForm
  [_this _props]
  {fo/id           m.core-tx-out/id
   fo/route-prefix "node-tx-core-tx-out"
   fo/title        "Outputs"
   fo/attributes   [m.core-tx-out/value
                    m.core-tx-out/n
                    m.core-tx-out/asm
                    m.core-tx-out/hex
                    m.core-tx-out/type]})

(form/defsc-form CoreTxSubForm
  [_this _props]
  {fo/id           m.core-tx/id
   fo/route-prefix "ln-core-tx"
   fo/title        "Core Transaction"
   fo/attributes   [m.core-tx/fetched?
                    m.core-tx/tx-id
                    m.core-tx/block
                    j.core-tx/ins
                    j.core-tx/outs]
   fo/field-styles {::m.core-tx/block :link}
   fo/subforms     {::m.core-tx/block {fo/ui u.core-block/CoreBlockSubForm}
                    ::m.core-tx/ins   {fo/ui CoreTxInSubForm}
                    ::m.core-tx/outs  {fo/ui CoreTxOutSubForm}}})

(def fetch-button
  {:type   :button
   :local? true
   :label  "Fetch"
   :action (fn [this _key]
             (let [{::m.core-tx/keys [id]} (comp/props this)]
               (comp/transact! this [(mu.core-tx/fetch! {::m.core-tx/id id})])))})

(def override-input true)

(form/defsc-form CoreTxInput
  [this props]
  {fo/id           m.core-tx-in/id
   fo/route-prefix "core-tx-in"
   fo/attributes   [m.core-tx-in/coinbase
                    m.core-tx-in/txinwitness
                    m.core-tx-in/sequence
                    m.core-tx-in/txid]
   fo/title        "Input"}
  (if override-input
    (form/render-layout this props)
    (let [{::m.core-tx-in/keys [sequence txid vout script-pub-key]} props]
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
  {fo/id           m.core-block/id
   fo/route-prefix "core-tx-block"
   fo/attributes   [m.core-block/height m.core-block/hash]
   fo/title        "Block"})

(def override-form false)

(form/defsc-form CoreTxForm
  [this props]
  {fo/id             m.core-tx/id
   fo/action-buttons (concat [::fetch] form/standard-action-buttons)
   fo/attributes     [m.core-tx/tx-id
                      m.core-tx/fetched?
                      m.core-tx/block
                      m.core-tx/hash
                      m.core-tx/hex
                      m.core-tx/lock-time
                      m.core-tx/size
                      m.core-tx/time
                      m.core-tx/version
                      j.core-tx/ins
                      j.core-tx/outs
                      j.core-tx/node]
   fo/cancel-route   ["core-txes"]
   fo/controls       (merge form/standard-controls {::fetch fetch-button})
   fo/field-styles   {::m.core-tx/block :link
                      ::m.core-tx/outs  :tx-out-table
                      ::m.core-tx/ins   :tx-in-table}
   fo/route-prefix   "core-tx"
   fo/subforms       {::m.core-tx/block {fo/ui CoreTxBlock}
                      ::m.core-tx/ins   {fo/ui CoreTxInput}
                      ::m.core-tx/outs  {fo/ui u.core-tx-out/CoreTxOutput}
                      ::m.core-tx/node  {fo/ui u.links/CoreNodeLinkForm}}
   fo/title          "Core Transaction"}
  (if override-form
    (form/render-layout this props)
    (dom/div {}
      (dom/p {} "foo")
      (form/render-layout this props))))

(defn fetch-action
  [report-instance {::m.core-tx/keys [id]}]
  (comp/transact! report-instance [(mu.core-tx/fetch! {::m.core-tx/id id})]))

(defn delete-action
  [report-instance {::m.core-tx/keys [id]}]
  (form/delete! report-instance ::m.core-tx/id id))

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
                    [(mu.core-tx/search!
                      {::m.core-tx/block block-id
                       ::m.core-tx/tx-id txid-value})])
    (control/run! this)))

(def search-control
  {:type   :button
   :label  "Search"
   :action search-control-action})

(report/defsc-report CoreTxReport
  [_this _props]
  {ro/columns [m.core-tx/tx-id
               j.core-tx/node
               m.core-tx/fetched?
               m.core-tx/block]
   ro/controls
   {::search search-control
    ::refresh
    {:type   :button
     :label  "Refresh"
     :action (fn [this] (control/run! this))}
    ::tx-id
    {:type          :string
     :style         :search
     :default-value ""
     :label         "Transaction Id"
     :onChange      (fn [this _] (control/run! this))}}
   ro/control-layout   {:inputs         [[::tx-id ::search]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.core-tx/block (fn [_this props]
                                            (log/debug :formatting {:props props})
                                            (u.links/ui-block-height-link props))
                        ::m.core-tx/node  (fn [_this props] (u.links/ui-core-node-link props))}
   ro/form-links       {::m.core-tx/tx-id CoreTxForm}
   ro/source-attribute ::m.core-tx/index
   ro/title            "Core Transactions"
   ro/row-actions      [fetch-action-button delete-action-button]
   ro/row-pk           m.core-tx/id
   ro/run-on-mount?    true
   ro/route            "core-txes"})
