(ns dinsro.ui.settings.ln.payreqs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.payreqs :as j.ln.payreqs]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-id :settings-ln-payreqs)
(def model-key ::m.ln.payreqs/id)
(def parent-router-id :settings-ln)
(def required-role :user)
(def show-page-id :settings-ln-payreqs-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.payreqs/node         #(u.links/ui-node-link %2)
                         ::m.ln.payreqs/payment-hash #(u.links/ui-payreq-link %3)}
   ro/columns           [m.ln.payreqs/payment-hash
                         m.ln.payreqs/description
                         m.ln.payreqs/payment-request
                         m.ln.payreqs/num-satoshis
                         m.ln.payreqs/node]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh  u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.ln.payreqs/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.payreqs/index
   ro/title             "Payment Request"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this props]
  {:ident         ::m.ln.payreqs/id
   :initial-state {::m.ln.payreqs/id nil}
   :query         [::m.ln.payreqs/id]}
  (log/debug :Show/starting {:props props})
  (dom/div {}
    "TODO: Show pay request"))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.navlinks/id index-page-id
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["requests"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["payreqs" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Payreqs"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
