(ns dinsro.ui.accounts.transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]))

;; [[../joins/transactions.cljc][Transaction Joins]]
;; [[../model/accounts.cljc][Accounts Model]]
;; [[../model/transactions.cljc][Transactions Model]]

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.transactions/description
                        j.transactions/debit-count
                        m.transactions/date]
   ro/control-layout   {:inputs         [[::m.accounts/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::m.accounts/id {:type :uuid :label "id"}
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.transactions/index
   ro/title            "Transactions"})

(defsc SubPage
  [_this {:ui/keys   [report]}]
  {:componentDidMount #(report/start-report! % Report {:route-params %})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {::m.accounts/id nil
                       :ui/report      {}}
   :query             [::m.accounts/id
                       {:ui/report (comp/get-query Report)}]}
  ((comp/factory Report) report))

(def ui-sub-page (comp/factory SubPage))
