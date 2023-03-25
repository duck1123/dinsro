(ns dinsro.ui.currencies.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.currencies/id)
(def router-key :dinsro.ui.currencies/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.accounts/name
                        m.accounts/currency
                        j.accounts/transaction-count]
   ro/controls         {::m.currencies/id {:type :uuid :label "id"}
                        ::refresh         u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.accounts/name     #(u.links/ui-account-link %3)
                        ::m.accounts/currency #(u.links/ui-currency-link %2)}
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/index
   ro/title            "Accounts"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [::m.currencies/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["accounts"]
   :initial-state     {:ui/report        {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
