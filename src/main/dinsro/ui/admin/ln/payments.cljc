(ns dinsro.ui.admin.ln.payments
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.payments :as j.ln.payments]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.ln.payments :as o.ln.payments]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/ln/payments.cljc]]
;; [[../../../model/ln/payments.cljc]]

(def index-page-id :admin-ln-payments)
(def model-key ::m.ln.payments/id)
(def parent-router-id :admin-ln)
(def required-role :admin)
(def show-page-id :admin-ln-payments-show)

(report/defsc-report Report
  [this _props]
  {ro/columns          [m.ln.payments/payment-hash
                        m.ln.payments/node
                        m.ln.payments/status]
   ro/field-formatters {o.ln.payments/node         #(u.links/ui-node-link %2)
                        o.ln.payments/payment-hash #(u.links/ui-payment-link %3)}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.ln.payments/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.ln.payments/index
   ro/title            "Payments"}
  (dom/div {}
    (report/render-layout this)))

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.payments/id
   :initial-state (fn [props]
                    {model-key (model-key props)})
   :query         (fn []
                    [model-key])
   :route-segment ["payments" :id]}
  (dom/div {}
    "Show Payment"))

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
   :route-segment     ["payments"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (dom/div {}
    (ui-report report)))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Payments"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Payment"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
