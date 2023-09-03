(ns dinsro.ui.admin.ln.payreqs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.payreqs :as j.ln.payreqs]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.mutations.ln.payreqs :as mu.ln.payreqs]
   [dinsro.options.ln.payreqs :as o.ln.payreqs]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/payreqs.cljc]]
;; [[../../../model/ln/payreqs.cljc]]

(def index-page-id :admin-ln-payreqs)
(def model-key o.ln.payreqs/id)
(def parent-router-id :admin-ln)
(def required-role :admin)
(def show-page-id :admin-ln-payreqs-show)

(def decode-button
  {:type   :button
   :local? true
   :label  "Decode"
   :action (fn [this _]
             (let [props (comp/props this)]
               (log/info :decode-button/clicked {:props props})
               (comp/transact! this [`(mu.ln.payreqs/decode ~props)])))})

(form/defsc-form NewForm
  [_this _props]
  {fo/action-buttons [::decode]
   fo/attributes     [m.ln.payreqs/payment-request]
   fo/controls       {::decode decode-button}
   fo/id             m.ln.payreqs/id
   fo/route-prefix   "new-request"
   fo/title          "New Payreqs"})

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.ln.payreqs/payment-hash
                        m.ln.payreqs/description
                        m.ln.payreqs/payment-request
                        m.ln.payreqs/num-satoshis
                        m.ln.payreqs/node]
   ro/field-formatters {o.ln.payreqs/node #(u.links/ui-node-link %2)
                        o.ln.payreqs/payment-hash #(u.links/ui-payreq-link %3)}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route            "requests"
   ro/row-pk           m.ln.payreqs/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.payreqs/index
   ro/title            "Payment Request"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.payreqs/id
   :initial-state (fn [props] {model-key (model-key props)})
   :query         (fn [] [model-key])
   :route-segment ["payreqs" :id]}
  (dom/div {}))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["requests"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Payment Requests"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Payment Request"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
