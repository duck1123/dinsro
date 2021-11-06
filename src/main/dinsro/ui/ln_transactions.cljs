(ns dinsro.ui.ln-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.attributes :as attr :refer [defattr]]
   [com.fulcrologic.rad.attributes-options :as ao]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln-nodes :as m.ln-nodes]
   [dinsro.model.ln-transactions :as m.ln-tx]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defsc LnTxRow
  [_this {::m.ln-tx/keys [label time-stamp tx-hash] :as props}]
  {}
  (dom/tr {}
    (comment (dom/td (str (keys props))))
    (dom/td label)
    (dom/td time-stamp)
    (dom/td tx-hash)))

(def ui-ln-tx-row (comp/factory LnTxRow {:keyfn ::m.ln-tx/id}))

(defn ref-ln-tx-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (comment (dom/th {} "keys"))
         (dom/th {} "label")
         (dom/th {} "timestamp")
         (dom/th {} "tx-hash")))
     (dom/tbody {}
       (for [tx value]
         (ui-ln-tx-row tx))))))

(def render-ref-ln-tx-row (render-field-factory ref-ln-tx-row))

(defattr transaction-node-link ::m.ln-tx/node :ref
  {ao/cardinality      :one
   ao/identities       #{::m.ln-tx/id}
   ao/target           ::m.ln-nodes/id
   ::report/column-EQL {::m.ln-tx/node (comp/get-query u.links/NodeLink)}})

(form/defsc-form LNTransactionForm
  [_this _props]
  {fo/id           m.ln-tx/id
   fo/attributes   [m.ln-tx/id
                    m.ln-tx/amount
                    m.ln-tx/tx-hash
                    m.ln-tx/num-confirmations
                    m.ln-tx/block-height
                    m.ln-tx/block-hash
                    m.ln-tx/time-stamp
                    m.ln-tx/raw-tx-hex
                    m.ln-tx/label]
   fo/layout-styles {:ref-container :tablef}
   fo/route-prefix "ln-tx"
   fo/title        "Lightning Node"})

(report/defsc-report LNTransactionsReport
  [_this _props]
  {ro/columns          [m.ln-tx/id
                        m.ln-tx/amount
                        m.ln-tx/tx-hash
                        transaction-node-link
                        ;; m.ln-tx/node
                        ]
   ro/field-formatters
   {::m.ln-tx/node     (fn [_this props] (u.links/ui-node-link props))}
   ro/links            {::m.ln-tx/id (fn [this props]
                                       (let [{::m.ln-tx/keys [id]} props]
                                         (form/view! this LNTransactionForm id)))}
   ro/route            "ln-transactions"
   ro/row-actions      []
   ro/row-pk           m.ln-tx/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln-tx/all-txes
   ro/title            "Lightning Transactions"})
