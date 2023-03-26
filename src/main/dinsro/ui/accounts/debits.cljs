(ns dinsro.ui.accounts.debits
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.accounts :as m.accounts]
   ;; [dinsro.model.currencies :as m.currencies]
   [dinsro.model.debits :as m.debits]
   ;; [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.accounts/id)
(def router-key :dinsro.ui.accounts/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.debits/value]
   ro/control-layout   {:inputs         [[::m.accounts/id]]
                        :action-buttons [::refresh]}
   ro/controls         {::m.accounts/id {:type :uuid :label "id"}
                        ::refresh       u.links/refresh-control}
   ;; ro/field-formatters {::m.debits/description #(u.links/ui-transaction-link %3)}
   ro/row-pk           m.debits/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.debits/index
   ro/title            "Debits"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :ident             (fn [] [:component/id ::SubPage])
   :initial-state     {:ui/report {}}
   :query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["debits"]}
  ((comp/factory Report) report))
