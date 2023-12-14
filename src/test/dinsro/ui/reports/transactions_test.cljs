(ns dinsro.ui.reports.transactions-test
  (:require
   [dinsro.mocks.debits :as mo.debits]
   [dinsro.mocks.transactions :as mo.transactions]
   [dinsro.test-helpers :as th]
   [dinsro.ui.reports.transactions :as u.r.transactions]
   [nubank.workspaces.core :as ws]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionsCurrencyInfo
  (th/fulcro-card u.r.transactions/CurrencyInfo mo.debits/CurrencyInfo-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionsAccountInfo
  (th/fulcro-card u.r.transactions/AccountInfo mo.debits/AccountInfo-data {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionsDebitList-Line
  (th/fulcro-card u.r.transactions/DebitLine-List mo.debits/make-debit-list-line {}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionsBodyItem
  (th/fulcro-card u.r.transactions/BodyItem mo.transactions/make-body-item {}))
