(ns dinsro.ui.transactions-test
  (:require
   ;; [dinsro.mocks.debits :as mo.debits]
   [dinsro.mocks.transactions :as mo.transactions]
   [dinsro.test-helpers :as th]
   [dinsro.ui.transactions :as u.transactions]
   [nubank.workspaces.core :as ws]))

;; [[../../../main/dinsro/mocks/ui/transactions.cljc]]
;; [[../../../main/dinsro/ui/transactions.cljc]]

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard ShowTransaction
  (th/fulcro-card u.transactions/Show mo.transactions/make-transaction {}))
