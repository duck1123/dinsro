(ns dinsro.ui.admin.nostr.filter-items
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.filter-items :as j.n.filter-items]
   [dinsro.model.nostr.filter-items :as m.n.filter-items]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/columns          [m.n.filter-items/filter
                        m.n.filter-items/index
                        m.n.filter-items/kind
                        m.n.filter-items/type
                        m.n.filter-items/event
                        m.n.filter-items/pubkey
                        j.n.filter-items/request]
   ro/control-layout   {:action-buttons [::refresh]}
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.n.filter-items/filter  #(u.links/ui-filter-link %2)
                        ::m.n.filter-items/pubkey  #(and %2 (u.links/ui-pubkey-link %2))
                        ::j.n.filter-items/request #(and %2 (u.links/ui-request-link %2))}
   ro/machine          spr/machine
   ro/page-size        10
   ro/paginate?        true
   ro/route            "filter-items"
   ro/row-pk           m.n.filter-items/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.n.filter-items/index
   ro/title            "Filter Items"})
