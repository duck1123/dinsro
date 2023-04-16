(ns dinsro.ui.nostr.witnesses
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.links :as u.links]))

;; [../../model/nostr/witnesses.cljc](Witness Model)
;; [../../queries/nostr/witnesses.clj]

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.witnesses/id
                        m.n.witnesses/event
                        m.n.witnesses/run]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/route            "witnesses"
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/row-pk           m.n.witnesses/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.witnesses/index
   ro/title            "Witnesses"})
