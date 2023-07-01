(ns dinsro.ui.admin.rate-sources
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.rate-sources :as j.rate-sources]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]))

;; [[../../joins/rate_sources.cljc]]
;; [[../../model/rate_sources.cljc]]
;; [[../../mutations/rate_sources.cljc]]

(def model-key ::m.rate-sources/id)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.rate-sources/name
                        j.rate-sources/rate-count]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/route            "rate-sources"
   ro/row-actions      [(u.buttons/row-action-button "Delete" model-key mu.rate-sources/delete!)]
   ro/row-pk           m.rate-sources/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.rate-sources/index
   ro/title            "Rate Sources"})
