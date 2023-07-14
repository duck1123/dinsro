(ns dinsro.ui.admin.users.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/transactions.cljc]]
;; [[../../../model/transactions.cljc]]

(def ident-key ::m.users/id)
(def index-page-key :admin-users-show-transactions)
(def model-key ::m.transactions/id)
(def parent-model-key ::m.users/id)
(def router-key :dinsro.ui.admin.users/Router)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/columns           [m.transactions/description
                         m.transactions/date]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.users/id {:type :uuid :label "id"}
                         ::refresh    u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.transactions/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.transactions/index
   ro/title             "Transactions"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {::m.users/keys [id]
          :ui/keys       [report]
          :as            props}]
  {:componentDidMount (partial u.loader/subpage-loader ident-key router-key Report)
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       ::m.users/id    nil
                       :ui/report      {}}
   :query             [[::dr/id router-key]
                       ::m.navlinks/id
                       ::m.users/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["transactions"]
   :will-enter        (u.loader/targeted-subpage-loader index-page-key parent-model-key ::SubPage)}
  (log/info :SubPage/starting {:props props})
  (if (and report id)
    (ui-report report)
    (u.debug/load-error props "admin user transactions")))

(m.navlinks/defroute   :admin-users-show-transactions
  {::m.navlinks/control       ::SubPage
   ::m.navlinks/label         "Transactions"
   ::m.navlinks/model-key     ::m.transactions/id
   ::m.navlinks/parent-key    :admin-users-show
   ::m.navlinks/router        :admin-users
   ::m.navlinks/required-role :admin})
