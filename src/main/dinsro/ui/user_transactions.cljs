(ns dinsro.ui.user-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.transactions :as j.transactions]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.transactions/description
                        m.transactions/date
                        j.transactions/user]
   ro/controls         {::m.users/id {:type :uuid :label "id"}
                        ::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.transactions/description #(u.links/ui-transaction-link %3)}
   ro/row-pk           m.transactions/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.transactions/index
   ro/title            "User Transactions"})

(def ui-report (comp/factory Report))

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.users/Router)

(defsc SubPage
  [_this {:ui/keys [report] :as props}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["transactions"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (if (get-in props [[::dr/id router-key] ident-key])
    (ui-report report)
    (dom/div  :.ui.segment
      (dom/h3 {} "Node ID not set")
      (u.links/log-props props))))
