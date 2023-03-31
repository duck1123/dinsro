(ns dinsro.ui.admin.nostr.runs
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.runs :as j.n.runs]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.runs/id]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/route            "runs"
   ro/row-pk           m.n.runs/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.runs/index
   ro/title            "Runs"})
