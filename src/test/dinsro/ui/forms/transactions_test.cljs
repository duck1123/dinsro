(ns dinsro.ui.forms.transactions-test
  (:require
   [dinsro.mocks.ui.forms.transactions :as mo.u.f.transactions]
   [dinsro.test-helpers :as th]
   [dinsro.ui.forms.transactions :as u.f.transactions]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

;; [[../../../../main/dinsro/mocks/ui/forms/transactions.cljc]]
;; [[../../../../main/dinsro/ui/forms/transactions.cljc]]

(ws/defcard NewDebit
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.transactions/NewDebit mo.u.f.transactions/NewDebit-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard NewTransaction
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.transactions/NewTransaction mo.u.f.transactions/NewTransaction-data {}))

(ws/defcard EditForm
  {::wsm/card-width 3 ::wsm/card-height 10}
  (th/fulcro-card u.f.transactions/EditForm mo.u.f.transactions/EditForm-data {}))
