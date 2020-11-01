(ns dinsro.ui.index-transactions-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard IndexTransactions
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-transactions/IndexTransactions
    ::ct.fulcro3/initial-state
    (fn [] {:users []})
    ::ct.fulcro3/wrap-root? false}))
