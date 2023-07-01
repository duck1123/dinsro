(ns dinsro.ui.admin.debits
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.ui.links :as u.links]))

;; [[../../joins/debits.cljc]]
;; [[../../model/debits.cljc]]

(def model-key ::m.debits/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.debits/account     #(u.links/ui-account-link %2)
                         ::m.debits/transaction #(u.links/ui-transaction-link %2)}
   ro/columns           [m.debits/value
                         m.debits/account
                         m.debits/transaction]
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "debits"
   ro/row-pk            m.debits/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.debits/admin-index
   ro/title             "Admin Debits Report"})
