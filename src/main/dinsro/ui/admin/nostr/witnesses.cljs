(ns dinsro.ui.admin.nostr.witnesses
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.witnesses :as j.n.witnesses]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.ui.links :as u.links]))

;; [[../../../actions/nostr/witnesses.clj]]
;; [[../../../joins/nostr/witnesses.cljc]]
;; [[../../../model/nostr/witnesses.cljc]]

(def model-key ::m.n.witnesses/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.witnesses/event #(u.links/ui-event-link %2)
                         ::m.n.witnesses/run   #(u.links/ui-run-link %2)
                         ::j.n.witnesses/relay #(u.links/ui-relay-link %2)}
   ro/columns           [m.n.witnesses/event
                         m.n.witnesses/run
                         j.n.witnesses/relay]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/route             "witnesses"
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.witnesses/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.witnesses/index
   ro/title             "Witnesses"})
