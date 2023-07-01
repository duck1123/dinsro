(ns dinsro.ui.ln.accounts
  (:require
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.ln.accounts :as j.ln.accounts]
   [dinsro.model.ln.accounts :as m.ln.accounts]
   [dinsro.ui.links :as u.links]))

;; [[../../joins/ln/accounts.cljc]]
;; [[../../model/ln/accounts.cljc]]

(def model-key ::m.ln.accounts/id)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.ln.accounts/node #(u.links/ui-node-link %2)}
   ro/columns           [m.ln.accounts/id
                         m.ln.accounts/node]
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/route             "accounts"
   ro/row-pk            m.ln.accounts/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.ln.accounts/index
   ro/title             "Lightning Accounts Report"})
