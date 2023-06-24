(ns dinsro.ui.admin.nostr.badge-acceptances
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.badge-acceptances :as j.n.badge-acceptances]
   [dinsro.model.nostr.badge-acceptances :as m.n.badge-acceptances]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-acceptances/id]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/route            "badge-acceptances"
   ro/row-pk           m.n.badge-acceptances/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-acceptances/index
   ro/title            "Acceptances"})
