(ns dinsro.ui.ln.payreqs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.rendering.semantic-ui.field :refer [render-field-factory]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.mutations.ln.payreqs :as mu.ln.payreqs]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(defsc LnPayreqRow
  [_this {::m.ln.payreqs/keys [description
                               num-satoshis
                               payment-request]}]
  {}
  (dom/tr {}
    (dom/td (str description))
    (dom/td (str num-satoshis))
    (dom/td (str (apply str (take 80 payment-request)) "..."))))

(def ui-ln-payreq-row (comp/factory LnPayreqRow {:keyfn ::m.ln.payreqs/id}))

(defn ref-ln-payreq-row
  [{:keys [value]} _attribute]
  (comp/fragment
   (dom/table :.ui.table
     (dom/thead {}
       (dom/tr {}
         (dom/th {} "description")
         (dom/th {} "num satoshis")
         (dom/th {} "payment request")))
     (dom/tbody {}
       (for [tx value]
         (ui-ln-payreq-row tx))))))

(def render-ref-ln-payreq-row (render-field-factory ref-ln-payreq-row))

(def pay-button
  {:type   :button
   :local? true
   :label  "Pay"
   :action (fn [this _]
             (let [props (comp/props this)]
               (comp/transact! this [(mu.ln.payreqs/submit! props)])))})

(def decode-button
  {:type   :button
   :local? true
   :label  "Decode"
   :action (fn [this _]
             (let [props (comp/props this)]
               (log/info :decode-button/clicked {:props props})
               (comp/transact! this [(mu.ln.payreqs/decode props)])))})

(form/defsc-form NewPaymentForm [_this _props]
  {fo/id             m.ln.payreqs/id
   fo/action-buttons [::decode]
   fo/controls       {::decode decode-button}
   fo/attributes     [m.ln.payreqs/payment-request
                      m.ln.payreqs/node]
   fo/route-prefix   "new-payment"
   fo/title          "New Payreqs"})

(report/defsc-report LNPayreqsReport
  [this _props]
  {ro/columns          [m.ln.payreqs/payment-hash
                        m.ln.payreqs/description
                        m.ln.payreqs/payment-request
                        m.ln.payreqs/num-satoshis
                        m.ln.payreqs/node]
   ro/field-formatters {::m.ln.payreqs/node #(u.links/ui-node-link %2)
                        ::m.ln.payreqs/payment-hash #(u.links/ui-payreq-link %3)}
   ro/route            "payreqs"
   ro/row-pk           m.ln.payreqs/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.ln.payreqs/index
   ro/title            "Lightning Payreqs"}
  (dom/div {}
    (dom/h1 {} "Payreqs")
    (report/render-layout this)))

(defsc ShowPayreq
  [_this _props]
  {:ident ::m.ln.payreqs/id
   :query [::m.ln.payreqs/id]
   :initial-state {::m.ln.payreqs/id nil}}
  (dom/div {}))
