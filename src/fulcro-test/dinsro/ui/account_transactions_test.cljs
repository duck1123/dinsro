(ns dinsro.ui.account-transactions-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.account-transactions :as u.account-transactions]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AccountTransactions
  {::wsm/align {:flex 1}
   ::wsm/card-height 15
   ::wsm/card-width 5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.account-transactions/AccountTransactions
    ::ct.fulcro3/initial-state
    (fn [] {:current-account 1
            :button-data {}
            :form-data {}
            :transaction-data {:transactions (map sample/transaction-map [1 2])}})
    ::ct.fulcro3/wrap-root? false}))
