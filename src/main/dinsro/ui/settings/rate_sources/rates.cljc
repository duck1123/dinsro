(ns dinsro.ui.settings.rate-sources.rates
  (:require
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.rates :as j.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.ui.charts :as u.charts]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [this props]
  {ro/columns           [m.rates/rate
                         m.rates/date]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::m.rate-sources/id {:type :uuid :label "id"}
                         ::refresh           u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.rates/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.rates/index
   ro/title             "Rates"}
  (let [{:ui/keys [current-rows]} props]
    (comp/fragment
     ((report/control-renderer this) this)
     (let [values (map (fn [{::m.rates/keys [date rate]}] {:date date :rate rate}) current-rows)]
       (u.charts/ui-rate-chart {:rates values :height 300 :width 500})))))

(def ui-report (comp/factory Report))
