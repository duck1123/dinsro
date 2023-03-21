(ns dinsro.ui.transactions.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.debits/value
                        j.debits/currency
                        m.debits/account]
   ro/controls         {::m.transactions/id {:type :uuid :label "Block"}
                        ::refresh           u.links/refresh-control}
   ro/control-layout   {:inputs         [[::m.transactions/id]]
                        :action-buttons [::refresh]}
   ro/field-formatters {::m.debits/value    #(u.links/ui-debit-link %3)
                        ::m.debits/account  #(u.links/ui-account-link %2)
                        ::j.debits/currency #(u.links/ui-currency-link %2)}
   ro/row-pk           m.debits/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.debits/index
   ro/title            "Transaction Debits"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]
          ::m.transactions/keys [id]}]
  {:query             [::m.transactions/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.transactions/id nil
                       :ui/report   {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if id
    (ui-report report)
    (dom/p "No id set")))

(def ui-sub-page (comp/factory SubPage))
