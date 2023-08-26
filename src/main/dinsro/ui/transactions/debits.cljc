(ns dinsro.ui.transactions.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.controls :as u.controls]
   [dinsro.ui.links :as u.links]))

(def index-page-id :transactions-show-debits)
(def parent-model-key ::m.transactions/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.debits/value    #(u.links/ui-debit-link %3)
                         ::m.debits/account  #(u.links/ui-account-link %2)
                         ::j.debits/currency #(u.links/ui-currency-link %2)}
   ro/columns           [m.debits/value
                         j.debits/currency
                         m.debits/account]
   ro/control-layout    {:inputs         [[::m.transactions/id]]
                         :action-buttons [::refresh]}
   ro/controls          {::m.transactions/id {:type :uuid :label "Block"}
                         ::refresh           u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.debits/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.debits/index
   ro/title             "Transaction Debits"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this props]
  {:componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :ident             (fn [] [::m.navlinks/id index-page-id])
   :initial-state     {::m.transactions/id nil
                       ::m.navlinks/id     index-page-id
                       :ui/report          {}}
   :query             [::m.transactions/id
                       ::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]}
  (u.controls/sub-page-report-loader props ui-report parent-model-key :ui/report))

(def ui-sub-page (comp/factory SubPage))
