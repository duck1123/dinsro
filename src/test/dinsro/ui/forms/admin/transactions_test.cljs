(ns dinsro.ui.forms.admin.transactions-test
  (:require
   [dinsro.mocks.ui.forms.admin.transactions :as mo.u.f.a.transactions]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.admin.transactions :as u.f.a.transactions]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../../main/dinsro/mocks/ui/forms/admin/transactions.cljc]]
;; [[../../../../../main/dinsro/ui/forms/admin/transactions.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NewDebit
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.a.transactions/NewDebit mo.u.f.a.transactions/NewDebit-state {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard AdminTransactionForm
  {::wsm/card-width 5 ::wsm/card-height 12}
  (th/fulcro-card u.f.a.transactions/AdminTransactionForm mo.u.f.a.transactions/get-state {}))
