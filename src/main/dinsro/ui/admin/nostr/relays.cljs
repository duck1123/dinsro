(ns dinsro.ui.admin.nostr.relays
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.relays :as j.n.relays]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations.nostr.relays :as mu.n.relays]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.relays/address          #(u.links/ui-relay-link %3)
                         ::j.n.relays/connection-count #(u.links/ui-relay-connection-count-link %3)
                         ::j.n.relays/request-count    #(u.links/ui-relay-request-count-link %3)}
   ro/columns          [m.n.relays/address
                        m.n.relays/connected
                        j.n.relays/request-count
                        j.n.relays/connection-count]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/route            "relays"
   ro/row-actions      [(u.links/row-action-button "Delete" ::m.n.relays/id mu.n.relays/delete!)]
   ro/row-pk           m.n.relays/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.relays/index
   ro/title            "Relays Report"})
