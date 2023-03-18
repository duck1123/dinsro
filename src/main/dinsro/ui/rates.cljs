(ns dinsro.ui.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.model.rates :as m.rates]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.rates/rate
                        m.rates/source
                        m.rates/date]
   ro/field-formatters {::m.rates/currency #(u.links/ui-currency-link %2)
                        ::m.rates/source   #(u.links/ui-rate-source-link %2)}
   ro/route            "rates"
   ro/row-pk           m.rates/id
   ro/run-on-mount?    true
   ro/source-attribute ::m.rates/index
   ro/title            "Rates Report"})

(defsc ShowRate
  [_this _props]
  {:ident ::m.rates/id
   :query [::m.rates/id]
   :initial-state {::m.rates/id nil}
   :route-segment ["rates" :id]}
  (dom/div {}))
