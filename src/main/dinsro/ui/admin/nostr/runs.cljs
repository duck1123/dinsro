(ns dinsro.ui.admin.nostr.runs
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.runs :as j.n.runs]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.ui.links :as u.links]))

;; [../../../joins/nostr/runs.cljc]

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.runs/connection #(u.links/ui-connection-link %2)
                         ::m.n.runs/request    #(u.links/ui-request-link %2)
                         ::j.n.runs/relay      #(u.links/ui-relay-link %2)}
   ro/columns           [m.n.runs/status
                         m.n.runs/request
                         m.n.runs/connection
                         m.n.runs/start-time
                         m.n.runs/finish-time
                         m.n.runs/end-time
                         j.n.runs/relay]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/route             "runs"
   ro/row-pk            m.n.runs/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.runs/index
   ro/title             "Runs"})