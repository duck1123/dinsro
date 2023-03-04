(ns dinsro.ui.rate-sources.accounts
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.accounts :as j.accounts]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.accounts/name
                        m.accounts/currency
                        m.accounts/source
                        m.accounts/wallet]
   ro/controls         {::m.rate-sources/id {:type :uuid :label "id"}
                        ::refresh u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.accounts/name #(u.links/ui-account-link %3)}
   ro/row-pk           m.accounts/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.accounts/index-by-rate-source
   ro/title            "Rate Sources Accounts"})

(def ui-report (comp/factory Report))

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [::m.rate-sources/id
                       {:ui/report (comp/get-query Report)}]
   :componentDidMount #(report/start-report! % Report {:route-params %})
   :initial-state     {::m.rate-sources/id nil
                       :ui/report          {}}
   :ident             (fn [] [:component/id ::SubPage])}
  (ui-report report))

(def ui-sub-page (comp/factory SubPage))
