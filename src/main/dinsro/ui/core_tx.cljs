(ns dinsro.ui.core-tx
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.core-tx :as j.core-tx]
   [dinsro.model.core-block :as m.core-block]
   [dinsro.model.core-tx :as m.core-tx]
   [dinsro.model.core-tx-in :as m.core-tx-in]
   [dinsro.model.core-tx-out :as m.core-tx-out]
   [dinsro.mutations.core-tx :as mu.core-tx]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.core-block :as u.core-block]
   [dinsro.ui.core-tx-out :as u.core-tx-out]
   [dinsro.ui.links :as u.links]))

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

(report/defsc-report CoreTxReport
  [_this _props]
  {ro/columns          [m.core-tx/tx-id
                        j.core-tx/node
                        m.core-tx/fetched?
                        m.core-tx/block]
   ro/field-formatters {::m.core-tx/block (fn [_this props] (u.links/ui-block-link props))
                        ::m.core-tx/node  (fn [_this props] (u.links/ui-core-node-link props))}
   ro/form-links       {::m.core-tx/tx-id CoreTxForm}
   ro/source-attribute ::m.core-tx/index
   ro/title            "Core Transactions"
   ro/row-pk           m.core-tx/id
   ro/run-on-mount?    true
   ro/route            "core-txes"})
