(ns dinsro.ui.transactions.debits-test
  (:require
   [dinsro.client :as client]
   [dinsro.mocks.debits :as mo.debits]
   [dinsro.ui.transactions.debits :as u.t.debits]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionDebitsReport
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.t.debits/Report
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (mo.debits/make-debit-report))}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TransactionDebitsSubPage
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.t.debits/SubSection
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state
    (fn [] (mo.debits/make-sub-page))}))
