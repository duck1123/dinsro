(ns dinsro.ui.admin.users.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.admin.users/Router)

(report/defsc-report Report
  ;; "Debits belonging to a user"
  [_this _props]
  {ro/column-formatters {::m.debits/description #(u.links/ui-transaction-link %3)
                         ::m.debits/account     #(u.links/ui-account-link %2)
                         ::m.debits/transaction #(u.links/ui-transaction-link %2)
                         ::j.debits/currency    #(u.links/ui-currency-link %2)}
   ro/columns           [m.debits/account
                         m.debits/transaction
                         m.debits/value
                         j.debits/currency]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.users/id {:type :uuid :label "id"}
                         ::refresh    u.links/refresh-control}
   ro/row-pk            m.debits/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.debits/index
   ro/title             "Debits"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["debits"]}
  ((comp/factory Report) report))
