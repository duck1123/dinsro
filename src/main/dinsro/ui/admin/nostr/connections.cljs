(ns dinsro.ui.admin.nostr.connections
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.connections :as j.n.connections]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.connections/status #(name %2)
                         ::m.n.connections/relay  #(u.links/ui-relay-link %2)}
   ro/columns           [m.n.connections/id
                         m.n.connections/relay
                         m.n.connections/status
                         m.n.connections/start-time
                         m.n.connections/end-time]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/route             "connections"
   ro/row-pk            m.n.connections/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.connections/index
   ro/title             "Connections"})
