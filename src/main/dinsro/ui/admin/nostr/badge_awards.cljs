(ns dinsro.ui.admin.nostr.badge-awards
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.badge-awards :as j.n.badge-awards]
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]
   [dinsro.ui.links :as u.links]))

;; [[../../../joins/nostr/badge_awards.cljc]]
;; [[../../../model/nostr/badge_awards.cljc]]

(def model-key ::m.n.badge-awards/id)

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.badge-awards/id]
   ro/control-layout   {:action-buttons [::new ::fetch ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/route            "badge-awards"
   ro/row-pk           m.n.badge-awards/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.badge-awards/index
   ro/title            "Badge Awards"})
