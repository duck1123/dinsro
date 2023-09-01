(ns dinsro.ui.transactions-test
  (:require
   [dinsro.client :as client]
   [dinsro.mocks.debits :as mo.debits]
   [dinsro.mocks.transactions :as mo.transactions]
   [dinsro.ui.transactions :as u.transactions]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

;; [[../../../main/dinsro/ui/transactions.cljc]]

(ws/defcard TransactionsDebitListLine
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.transactions/DebitLine-List
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state (fn [] (mo.debits/make-debit-list-line))}))

(ws/defcard TransactionsBodyItem
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.transactions/BodyItem
    ::ct.fulcro3/app           {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state (fn [] (mo.transactions/make-body-item))}))

(ws/defcard ShowTransaction
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.transactions/Show
    ::ct.fulcro3/app  {:client-will-mount client/setup-RAD}
    ::ct.fulcro3/initial-state (fn [] (mo.transactions/make-transaction))}))
