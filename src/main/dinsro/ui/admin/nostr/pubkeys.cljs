(ns dinsro.ui.admin.nostr.pubkeys
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.pubkeys/hex     #(u.links/ui-pubkey-link %3)
                         ::m.n.pubkeys/name    #(u.links/ui-admin-pubkey-name-link %3)
                         ::m.n.pubkeys/picture #(u.links/img-formatter %3)}
   ro/columns           [m.n.pubkeys/picture
                         m.n.pubkeys/name
                         j.n.pubkeys/contact-count
                         j.n.pubkeys/event-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "pubkeys"
   ro/row-pk            m.n.pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.pubkeys/index
   ro/title             "Pubkeys"})
