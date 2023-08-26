(ns dinsro.ui.settings.ln.payreqs
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
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

(def index-page-id :settings-ln-payreqs)
(def model-key ::m.ln.payreqs/id)
(def parent-router-id :settings-ln)
(def required-role :user)
(def show-page-key :settings-ln-payreqs-show)

(def decode-button
  {:type   :button
   :local? true
   :label  "Decode"
   :action (fn [this _]
             (let [props (comp/props this)]
               (log/info :decode-button/clicked {:props props})
               (comp/transact! this [`(mu.ln.payreqs/decode ~props)])))})

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
  [_this {::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state {::m.navlinks/id     show-page-key
                   ::m.navlinks/target {}}
   :query         [::m.navlinks/id
                   {::m.navlinks/target (comp/get-query Show)}]
   :route-segment ["payreqs" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (log/debug :ShowPage/starting {:props props})
  (if target
    (ui-show target)
    (u.debug/load-error props "settings show payreqs")))

(m.navlinks/defroute index-page-id
  {::m.navlinks/control       ::IndexPage
   ::m.navlinks/label         "Payreqs"
   ::m.navlinks/model-key     model-key
   ::m.navlinks/parent-key    parent-router-id
   ::m.navlinks/router        parent-router-id
   ::m.navlinks/required-role required-role})
