(ns dinsro.ui.ln.payreqs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.payreqs :as j.ln.payreqs]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.mutations.ln.payreqs :as mu.ln.payreqs]
   [dinsro.ui.links :as u.links]
   [lambdaisland.glogc :as log]))

(def decode-button
  {:type   :button
   :local? true
   :label  "Decode"
   :action (fn [this _]
             (let [props (comp/props this)]
               (log/info :decode-button/clicked {:props props})
               (comp/transact! this [(mu.ln.payreqs/decode props)])))})

(form/defsc-form NewForm [_this _props]
  {fo/action-buttons [::decode]
   fo/attributes     [m.ln.payreqs/payment-request]
   fo/controls       {::decode decode-button}
   fo/id             m.ln.payreqs/id
   fo/route-prefix   "new-request"
   fo/title          "New Payreqs"})

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.payreqs/node         #(u.links/ui-node-link %2)
                         ::m.ln.payreqs/payment-hash #(u.links/ui-payreq-link %3)}
   ro/columns           [m.ln.payreqs/payment-hash
                         m.ln.payreqs/description
                         m.ln.payreqs/payment-request
                         m.ln.payreqs/num-satoshis
                         m.ln.payreqs/node]
   ro/route             "requests"
   ro/row-pk            m.ln.payreqs/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.payreqs/index
   ro/title             "Payment Request"})

(defsc Show
  [_this _props]
  {:ident         ::m.ln.payreqs/id
   :initial-state {::m.ln.payreqs/id nil}
   :query         [::m.ln.payreqs/id]
   :route-segment ["payreqs" :id]}
  (dom/div {}))
