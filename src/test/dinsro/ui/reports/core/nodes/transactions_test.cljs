(ns dinsro.ui.reports.core.nodes.transactions-test
  (:require
   [dinsro.mocks.ui.core.nodes.transactions :as mo.u.c.n.transactions]
   [dinsro.test-helpers :as th]
   [dinsro.ui.reports.core.nodes.transactions :as u.r.c.n.transactions]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodeTransactionsReport
  {::wsm/card-width 6 ::wsm/card-height 12}
  (th/fulcro-card u.r.c.n.transactions/Report mo.u.c.n.transactions/Report-data {}))
