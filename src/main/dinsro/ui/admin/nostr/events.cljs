(ns dinsro.ui.admin.nostr.events
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.events/pubkey
                        m.n.events/kind
                        m.n.events/content]
   ro/control-layout   {:action-buttons [::new ::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.n.events/pubkey  #(u.links/ui-pubkey-link %2)
                        ::m.n.events/note-id #(u.links/ui-event-link %3)}
   ro/route            "events"
   ro/row-pk           m.n.events/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.events/index
   ro/title            "Events"})
