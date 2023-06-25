(ns dinsro.ui.settings.ln.payments
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.ln.payments :as j.ln.payments]
   [dinsro.model.ln.payments :as m.ln.payments]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/ln/payments.cljc]]
;; [[../../../model/ln/payments.cljc]]

(def index-page-key :settings-ln-payments)
(def model-key ::m.ln.payments/id)
(def show-page-key :settings-ln-payments-show)

(report/defsc-report Report
  [this _props]
  {ro/column-formatters {::m.ln.payments/node         #(u.links/ui-node-link %2)
                         ::m.ln.payments/payment-hash #(u.links/ui-payment-link %3)}
   ro/columns           [m.ln.payments/payment-hash
                         m.ln.payments/node
                         m.ln.payments/status]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh  u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.payments/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.payments/index
   ro/title             "Payments"}
  (dom/div {}
    (report/render-layout this)))

(def ui-report (comp/factory Report))

(defsc Show
  [_this _props]
  {:ident         ::m.ln.payments/id
   :initial-state {::m.ln.payments/id nil}
   :query         [::m.ln.payments/id]}
  (dom/div {}
    "TODO: Payment settings"))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["payments"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["payments" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if target
    (ui-show target)
    (ui-segment {} "Failed to load page")))
