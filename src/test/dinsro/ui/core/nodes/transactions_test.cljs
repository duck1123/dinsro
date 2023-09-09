(ns dinsro.ui.core.nodes.transactions-test
  (:require
   [dinsro.mocks.ui.core.nodes.transactions :as mo.u.c.n.transactions]
   [dinsro.test-helpers :as th]
   [dinsro.ui.core.nodes.transactions :as u.c.n.transactions]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../../main/dinsro/mocks/ui/core/nodes/transactions.cljc]]
;; [[../../../../../main/dinsro/ui/core/nodes/transactions.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodeTransactionsReport
  {::wsm/card-width 6 ::wsm/card-height 12}
  (th/fulcro-card u.c.n.transactions/Report mo.u.c.n.transactions/Report-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CoreNodeTransactionsSubPage
  {::wsm/card-width 6 ::wsm/card-height 12}
  (th/fulcro-card u.c.n.transactions/SubPage mo.u.c.n.transactions/SubPage-data {}))
