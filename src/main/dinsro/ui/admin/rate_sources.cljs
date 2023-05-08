(ns dinsro.ui.admin.rate-sources
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.ui.links :as u.links]))

;; [../../mutations/rate_sources.cljc]

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        j.rate-sources/rate-count]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/route            "rate-sources"
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.rate-sources/index
   ro/title            "Rate Sources"})
