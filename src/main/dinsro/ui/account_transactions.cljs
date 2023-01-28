(ns dinsro.ui.account-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.join.transactions :as j.transactions]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]))

;; [[../model/accounts.cljc][Accounts Model]]
;; [[../model/transactions.cljc][Transactions Model]]

(def debug-page false)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.transactions/description
                        m.transactions/date]
   ro/control-layout   {:inputs         [[::m.accounts/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::m.accounts/id {:type :uuid :label "id"}
                        ::refresh       u.links/refresh-control}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.transactions/index
   ro/title            "Account Transactions"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys   [report]
          account-id ::m.accounts/id}]
  {:componentDidMount #(report/start-report! % Report {:route-params %})
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {::m.accounts/id nil
                       :ui/report      {}}
   :query             [::m.accounts/id
                       {:ui/report (comp/get-query Report)}]}
  (comp/fragment
   (when debug-page
     (dom/p {} (pr-str account-id)))
   (ui-report report)))

(def ui-sub-page (comp/factory SubPage))
