(ns dinsro.ui.ln-payments
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln-payments :as m.ln-payments]
   [dinsro.ui.links :as u.links]))

(defsc RefRow
  [_this {::m.ln-payments/keys [value status payment-index] :as props}]
  {:ident ::m.ln-payments/id
   :query [::m.ln-payments/id
           ::m.ln-payments/payment-hash
           ::m.ln-payments/value
           ::m.ln-payments/status
           ::m.ln-payments/payment-index]}
  (dom/tr {}
    (dom/td (str payment-index))
    (dom/td (u.links/ui-payment-link props))
    (dom/td (str value))
    (dom/td (str status))))

(def ui-ref-row (comp/factory RefRow {:keyfn ::m.ln-payments/id}))

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

(form/defsc-form PaymentSubForm [_this _props]
  {fo/id           m.ln-payments/id
   fo/attributes   [m.ln-payments/payment-hash
                    m.ln-payments/payment-preimage
                    m.ln-payments/payment-request
                    m.ln-payments/status
                    m.ln-payments/fee
                    m.ln-payments/value
                    m.ln-payments/payment-index
                    m.ln-payments/failure-reason
                    m.ln-payments/creation-date]
   fo/route-prefix "ln-payment"
   fo/title        "Lightning Payments"})

(form/defsc-form LNPaymentForm [_this _props]
  {fo/id           m.ln-payments/id
   fo/attributes   [m.ln-payments/payment-hash
                    m.ln-payments/payment-preimage
                    m.ln-payments/payment-request
                    m.ln-payments/status
                    m.ln-payments/fee
                    m.ln-payments/value
                    m.ln-payments/payment-index
                    m.ln-payments/failure-reason
                    m.ln-payments/creation-date
                    m.ln-payments/node]
   fo/subforms     {::m.ln-payments/node {fo/ui u.links/NodeLinkForm}}
   fo/route-prefix "ln-payment"
   fo/title        "Lightning Payments"})

(report/defsc-report LNPaymentsReport
  [this _props]
  {ro/columns          [m.ln-payments/payment-hash
                        m.ln-payments/node
                        m.ln-payments/status]
   ro/links            {::m.ln-payments/payment-hash (fn [this {::m.ln-payments/keys [id]}]
                                                       (form/view! this LNPaymentForm id))}
   ro/field-formatters {::m.ln-payments/node (fn [_this props] (u.links/ui-node-link props))}
   ro/route            "payments"
   ro/row-pk           m.ln-payments/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln-payments/index
   ro/title            "Lightning Payments"}
  (dom/div {}
    (dom/h1 {} "Payments")
    (report/render-layout this)))
