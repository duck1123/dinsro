(ns dinsro.ui.ln.payments
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.payments :as j.ln.payments]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [this _props]
  {ro/columns          [m.ln.payments/payment-hash
                        m.ln.payments/node
                        m.ln.payments/status]
   ro/field-formatters {::m.ln.payments/node         #(u.links/ui-node-link %2)
                        ::m.ln.payments/payment-hash #(u.links/ui-payment-link %3)}
   ro/route            "payments"
   ro/row-pk           m.ln.payments/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.payments/index
   ro/title            "Lightning Payments"}
  (dom/div {}
    (dom/h1 {} "Payments")
    (report/render-layout this)))

(defsc ShowPayment
  [_this _props]
  {:ident ::m.ln.payments/id
   :query [::m.ln.payments/id]
   :initial-state {::m.ln.payments/id nil}
   :route-segment ["payments" :id]}
  (dom/div {}))
