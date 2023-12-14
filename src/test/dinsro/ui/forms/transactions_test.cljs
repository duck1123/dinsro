(ns dinsro.ui.forms.transactions-test
  (:require
   [dinsro.mocks.ui.forms.transactions :as mo.u.f.transactions]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.transactions :as u.f.transactions]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/mocks/ui/forms/transactions.cljc]]
;; [[../../../../main/dinsro/ui/forms/transactions.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CreateTransactionDebitLine
  {::wsm/card-height 10 ::wsm/card-width 3}
  (th/fulcro-card u.f.transactions/CreateTransactionDebitLine mo.u.f.transactions/CreateTransactionDebitLine-data))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard CreateTransactionForm
  {::wsm/card-height 10 ::wsm/card-width 3}
  (th/fulcro-card u.f.transactions/CreateTransactionForm mo.u.f.transactions/CreateTransactionForm-data))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionsNewDebitForm
  {::wsm/card-height 10 ::wsm/card-width 3}
  (th/fulcro-card u.f.transactions/NewDebit mo.u.f.transactions/NewDebit-data))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionsNewTransactionForm
  {::wsm/card-height 21 ::wsm/card-width 9}
  (th/fulcro-card u.f.transactions/NewTransaction mo.u.f.transactions/NewTransaction-data))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionsEditForm
  {::wsm/card-height 10 ::wsm/card-width 3}
  (th/fulcro-card u.f.transactions/EditForm mo.u.f.transactions/EditForm-data))
