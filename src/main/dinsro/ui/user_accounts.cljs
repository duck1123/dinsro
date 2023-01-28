(ns dinsro.ui.user-accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.users :as m.users]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.users/id)
(def router-key :dinsro.ui.users/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.accounts/name]
   ro/controls         {::m.users/id {:type :uuid :label "id"}
                        ::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.accounts/name #(u.links/ui-account-link %3)}
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/index
   ro/title            "User Accounts"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [[::dr/id router-key]
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params (comp/props %)})
   :route-segment     ["accounts"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
