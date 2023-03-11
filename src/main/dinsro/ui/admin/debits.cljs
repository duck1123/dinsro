(ns dinsro.ui.admin.debits
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.debits :as j.debits]
   [dinsro.model.debits :as m.debits]
   [dinsro.ui.links :as u.links]))

(report/defsc-report AdminReport
  [_this _props]
  {ro/columns          [m.debits/value
                        m.debits/account
                        m.debits/transaction]
   ro/controls         {::refresh u.links/refresh-control}
   ro/field-formatters {::m.debits/account     #(u.links/ui-account-link %2)
                        ::m.debits/transaction #(u.links/ui-transaction-link %2)}
   ro/route            "debits"
   ro/row-actions      []
   ro/row-pk           m.debits/id
   ro/run-on-mount?    true
   ro/source-attribute ::j.debits/admin-index
   ro/title            "Admin Debits Report"})
