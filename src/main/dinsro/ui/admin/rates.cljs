(ns dinsro.ui.admin.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.rates :as j.rates]
   [dinsro.model.rates :as m.rates]
   [dinsro.ui.links :as u.links]))

;; [[../actions/rates.clj][Rate Actions]]
;; [[../joins/rates.cljc][Rate Joins]]
;; [[../model/rates.cljc][Rates Model]]

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.rates/currency #(u.links/ui-currency-link %2)
                         ::m.rates/source   #(u.links/ui-rate-source-link %2)
                         ::m.rates/date     #(u.links/ui-rate-link %3)}
   ro/columns           [m.rates/rate
                         m.rates/source
                         m.rates/date]
   ro/route             "rates"
   ro/row-pk            m.rates/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rates/index
   ro/title             "Rates Report"})

(defsc Show
  [_this _props]
  {:ident         ::m.rates/id
   :initial-state {::m.rates/id nil}
   :query         [::m.rates/id]
   :route-segment ["rates" :id]}
  (dom/div {} "Show Rate"))