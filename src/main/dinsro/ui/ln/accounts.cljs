(ns dinsro.ui.ln.accounts
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.ui.links :as u.links]))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.accounts/node #(u.links/ui-node-link %2)}
   ro/columns           [m.ln.accounts/id
                         m.ln.accounts/node]
   ro/route             "accounts"
   ro/row-pk            m.ln.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.accounts/index
   ro/title             "Lightning Accounts Report"})
