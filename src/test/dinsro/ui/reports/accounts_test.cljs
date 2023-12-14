(ns dinsro.ui.reports.accounts-test
  (:require
   [dinsro.mocks.ui.reports.accounts :as mo.u.r.accounts]
   [dinsro.test-helpers :as th]
   [dinsro.ui.reports.accounts :as u.r.accounts]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/mocks/ui/reports/accounts.cljc]]
;; [[../../../../main/dinsro/ui/reports/accounts.cljc]]

(ws/defcard AccountsDebitLine
  {::wsm/card-height 11 ::wsm/card-width 5}
  (th/fulcro-card u.r.accounts/DebitLine mo.u.r.accounts/DebitLine-data {}))

(ws/defcard AccountsBodyItem-list
  {::wsm/card-height 11 ::wsm/card-width 5}
  (th/fulcro-card u.r.accounts/BodyItem-list mo.u.r.accounts/BodyItem-list-data {}))

(ws/defcard AccountsBodyItem-table
  {::wsm/card-height 11 ::wsm/card-width 5}
  (th/fulcro-card u.r.accounts/BodyItem-table mo.u.r.accounts/BodyItem-table-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AccountsReport
  {::wsm/card-height 11 ::wsm/card-width 5}
  (th/fulcro-card u.r.accounts/Report mo.u.r.accounts/Report-data {}))
