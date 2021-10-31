(ns dinsro.ui.category-transactions-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.category-transactions :as u.category-transactions]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard CategoryTransactions
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.category-transactions/CategoryTransactions
    ::ct.fulcro3/initial-state
    (fn []
      {::u.category-transactions/transactions
       {::u.category-transactions/transactions (vals sample/transaction-map)}})}))
