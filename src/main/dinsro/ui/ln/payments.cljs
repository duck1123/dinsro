(ns dinsro.ui.ln.payments
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.ui.links :as u.links]))

(defsc RefRow
  [_this {::m.ln.payments/keys [value status payment-index] :as props}]
  {:ident ::m.ln.payments/id
   :query [::m.ln.payments/id
           ::m.ln.payments/payment-hash
           ::m.ln.payments/value
           ::m.ln.payments/status
           ::m.ln.payments/payment-index]}
  (dom/tr {}
    (dom/td (str payment-index))
    (dom/td (u.links/ui-payment-link props))
    (dom/td (str value))
    (dom/td (str status))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.ln.payments/id}))

(defn ref-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "Index")
         (dom/th {} "Hash")
         (dom/th {} "Value")
         (dom/th {} "Status")))
     (dom/tbody {}
       (for [tx value]
         (ui-ref-row tx))))))

(def render-ref-row (render-field-factory ref-row))

(report/defsc-report LNPaymentsReport
  [this _props]
  {ro/columns          [m.ln.payments/payment-hash
                        m.ln.payments/node
                        m.ln.payments/status]
   ro/field-formatters {::m.ln.payments/node         #(u.links/ui-node-link %2)
                        ::m.ln.payments/payment-hash #(u.links/ui-payment-link %3)}
   ro/route            "payments"
   ro/row-pk           m.ln.payments/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.payments/index
   ro/title            "Lightning Payments"}
  (dom/div {}
    (dom/h1 {} "Payments")
    (report/render-layout this)))

(defsc ShowPayment
  [_this _props]
  {:ident ::m.ln.payments/id
   :query [::m.ln.payments/id]
   :initial-state {::m.ln.payments/id nil}}
  (dom/div {}))
