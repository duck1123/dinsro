(ns dinsro.ui.admin.ln.payments
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.payments :as j.ln.payments]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/ln/payments.cljc]]
;; [[../../../model/ln/payments.cljc]]

(def index-page-key :admin-ln-payments)
(def model-key ::m.ln.payments/id)

(report/defsc-report Report
  [this _props]
  {ro/columns          [m.ln.payments/payment-hash
                        m.ln.payments/node
                        m.ln.payments/status]
   ro/field-formatters {::m.ln.payments/node         #(u.links/ui-node-link %2)
                        ::m.ln.payments/payment-hash #(u.links/ui-payment-link %3)}
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
   :initial-state {::m.ln.payments/id nil}
   :query         [::m.ln.payments/id]
   :route-segment ["payments" :id]}
  (dom/div {}
    "Show Payment"))

(defsc IndexPage
  [_this {:ui/keys [report]}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["payments"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (dom/div {}
    (ui-report report)))
