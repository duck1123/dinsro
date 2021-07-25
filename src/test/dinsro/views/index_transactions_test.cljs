(ns dinsro.views.index-transactions-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [dinsro.views.index-transactions :as v.index-transactions]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard IndexTransactionsPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 15
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-transactions/IndexTransactionsPage
    ::ct.fulcro3/initial-state
    (fn []
      {:page/id                             ::v.index-transactions/page
       ::v.index-transactions/form          {}
       ::v.index-transactions/toggle-button {}
       ::v.index-transactions/transactions
       {::u.index-transactions/transactions (vals sample/transaction-map)}})}))
