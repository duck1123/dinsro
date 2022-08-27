(ns dinsro.ui.user-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.transactions/description]
   ro/controls         {::m.users/id {:type :uuid :label "id"}
                        ::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.transactions/index
   ro/title            "User Transactions"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [::m.users/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :initial-state     {::m.users/id nil
                       :ui/report   {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (ui-report report))

(def ui-sub-page (comp/factory SubPage))
