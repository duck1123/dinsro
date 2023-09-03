(ns dinsro.ui.admin.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../joins/debits.cljc]]
;; [[../../model/debits.cljc]]

(def index-page-id :admin-debits)
(def model-key ::m.debits/id)
(def parent-router-id :admin)
(def required-role :admin)
(def show-page-key :admin-debits-show)
(def log-props? true)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.debits/value       #(u.links/ui-admin-debit-link %3)
                         ::m.debits/account     #(u.links/ui-admin-account-link %2)
                         ::m.debits/transaction #(u.links/ui-admin-transaction-link %2)}
   ro/columns           [m.debits/value
                         m.debits/account
                         m.debits/transaction]
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.debits/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.debits/admin-index
   ro/title             "Admin Debits Report"})

(def ui-report (comp/factory Report))

(defsc Show
  [_this props]
  {:ident         ::m.debits/id
   :initial-state {::m.debits/id          nil
                   ::m.debits/account     {}
                   ::m.debits/transaction {}
                   ::m.debits/value       0}
   :query         [::m.debits/id
                   ::m.debits/account
                   ::m.debits/transaction
                   ::m.debits/value]}
  (ui-segment {}
    "TODO: Show Debit"
    (when log-props?
      (u.debug/log-props props))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report      (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["debits"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/debug :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-key])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-key
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show)}])
   :route-segment ["debit" :id]
   :will-enter    (u.loader/targeted-page-loader show-page-key model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Debits"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-key
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Debit"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
