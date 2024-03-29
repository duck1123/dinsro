(ns dinsro.ui.admin.users.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.options.debits :as o.debits]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.users :as o.users]
   [dinsro.ui.controls :as u.controls]

   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]))

;; [[../../../joins/debits.cljc]]
;; [[../../../model/debits.cljc]]

(def index-page-id :admin-users-show-debits)
(def model-key o.debits/id)
(def parent-model-key o.users/id)
(def parent-router-id :admin-users-show)
(def required-role :admin)
(def router-key :dinsro.ui.admin.users/Router)

(report/defsc-report Report
  ;; "Debits belonging to a user"
  [_this _props]
  {ro/column-formatters {o.debits/account     #(u.links/ui-account-link %2)
                         o.debits/transaction #(u.links/ui-transaction-link %2)
                         ::j.debits/currency  #(u.links/ui-currency-link %2)}
   ro/columns           [m.debits/account
                         m.debits/transaction
                         m.debits/value
                         j.debits/currency]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {parent-model-key {:type :uuid :label "id"}
                         ::refresh        u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.debits/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.debits/index
   ro/title             "Debits"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount (partial u.loader/subpage-loader parent-model-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     (fn [props]
                        {o.navlinks/id  index-page-id
                         parent-model-key (parent-model-key props)
                         :ui/report       (comp/get-initial-state Report {})})
   :query             (fn []
                        [[::dr/id router-key]
                         o.navlinks/id
                         parent-model-key
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["debits"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-id parent-model-key ::SubPage)}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::SubPage
   o.navlinks/label         "Debits"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
