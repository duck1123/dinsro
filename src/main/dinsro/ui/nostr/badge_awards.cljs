(ns dinsro.ui.nostr.badge-awards
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.badge-definitions :as j.n.badge-definitions]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.model.nostr.badge-definitions :as m.n.badge-definitions]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-awards/id]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/route            "badge-awards"
   ro/row-pk           m.n.badge-definitions/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-definitions/index
   ro/title            "Badge Awards"})
