(ns dinsro.ui.rate-sources.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.rates :as j.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.ui.links :as u.links]))

(def ident-key ::m.rate-sources/id)
(def router-key :dinsro.ui.rate-sources/Router)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.rates/rate
                        m.rates/date
                        m.rates/source]
   ro/controls         {::m.rate-sources/id {:type :uuid :label "id"}
                        ::refresh           u.links/refresh-control}
   ro/control-layout   {:action-buttons [::refresh]}
   ro/field-formatters {::m.rates/date   #(u.links/ui-rate-link %3)
                        ::m.rates/source #(u.links/ui-rate-source-link %2)}
   ro/row-pk           m.rates/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.rates/index
   ro/title            "Rate Sources Rates"})

(defsc SubPage
  [_this {:ui/keys [report]}]
  {:query             [{:ui/report (comp/get-query Report)}
                       [::dr/id router-key]]
   :componentDidMount (partial u.links/subpage-loader ident-key router-key Report)
   :route-segment     ["rates"]
   :initial-state     {:ui/report {}}
   :ident             (fn [] [:component/id ::SubPage])}
  ((comp/factory Report) report))
