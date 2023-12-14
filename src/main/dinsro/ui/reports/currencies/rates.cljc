(ns dinsro.ui.reports.currencies.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.rates :as j.rates]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.rates/value #(u.links/ui-rate-link %3)}
   ro/columns           [m.rates/rate m.rates/date]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.currencies/id {:type :uuid :label "id"}
                         ::refresh         u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.rates/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rates/index
   ro/title             "Rates"})

(def ui-report (comp/factory Report))
