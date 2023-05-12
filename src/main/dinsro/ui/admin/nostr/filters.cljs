(ns dinsro.ui.admin.nostr.filters
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.filters :as j.n.filters]
   [dinsro.model.nostr.filters :as m.n.filters]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.filters/request #(u.links/ui-request-link %2)
                         ::m.n.filters/index   #(u.links/ui-filter-link %3)}
   ro/columns           [m.n.filters/index
                         m.n.filters/request
                         m.n.filters/since
                         m.n.filters/until
                         j.n.filters/item-count]
   ro/control-layout    {:action-buttons [::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/route             "filters"
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.filters/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.filters/index
   ro/title             "Filters"})
