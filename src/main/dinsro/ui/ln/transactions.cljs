(ns dinsro.ui.ln.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.ui.core.tx :as u.c.tx]
   [dinsro.ui.links :as u.links]))

(defsc LnTxRow
  [_this {::m.ln.tx/keys [amount core-tx time-stamp tx-hash]}]
  {}
  (dom/tr {}
    (dom/td (str amount))
    (dom/td (u.links/ui-core-tx-link core-tx))
    (dom/td (str time-stamp))
    (dom/td (str tx-hash))))

(def ui-ln-tx-row (comp/factory LnTxRow {:keyfn ::m.ln.tx/id}))

(defn ref-ln-tx-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "amount")
         (dom/th {} "core-tx")
         (dom/th {} "timestamp")
         (dom/th {} "tx-hash")))
     (dom/tbody {}
       (for [tx value]
         (ui-ln-tx-row tx))))))

(def render-ref-ln-tx-row (render-field-factory ref-ln-tx-row))

(def override-tx-subform true)

(form/defsc-form TxSubform
  [this {::m.ln.tx/keys [id amount]
         :as            props}]
  {fo/id           m.ln.tx/id
   fo/route-prefix "ln-tx"
   fo/title        "Transactions"
   fo/field-styles {::m.ln.tx/core-tx :link}
   fo/attributes   [m.ln.tx/amount
                    m.ln.tx/core-tx]
   fo/subforms     {::m.ln.tx/core-tx {fo/ui u.c.tx/CoreTxSubForm}}}
  (if override-tx-subform
    (form/render-layout this props)
    (dom/div :.ui
      (dom/p {} "Tx: " (str id))
      (dom/p {} "Amount: " amount))))

(form/defsc-form LNTransactionForm
  [_this _props]
  {fo/id            m.ln.tx/id
   fo/attributes    [m.ln.tx/node
                     m.ln.tx/core-tx
                     m.ln.tx/tx-hash
                     m.ln.tx/amount
                     m.ln.tx/block-height
                     m.ln.tx/block-hash
                     m.ln.tx/time-stamp
                     m.ln.tx/raw-tx-hex
                     m.ln.tx/label]
   fo/layout-styles {:ref-container :tablef}
   fo/subforms      {::m.ln.tx/core-tx {fo/ui u.links/CoreTxLinkForm}
                     ::m.ln.tx/node    {fo/ui u.links/NodeLinkForm}}
   fo/route-prefix  "ln-tx"
   fo/title         "Lightning TX"})

(report/defsc-report LNTransactionsReport
  [_this _props]
  {ro/columns          [m.ln.tx/core-tx
                        m.ln.tx/amount
                        m.ln.tx/node]
   ro/field-formatters
   {::m.ln.tx/node    (fn [_this props] (u.links/ui-node-link props))
    ::m.ln.tx/core-tx (fn [_this props] (u.links/ui-core-tx-link props))}
   ro/route            "ln-transactions"
   ro/row-actions      []
   ro/row-pk           m.ln.tx/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.tx/index
   ro/title            "Lightning Transactions"})
