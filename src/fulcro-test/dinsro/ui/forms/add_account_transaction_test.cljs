(ns dinsro.ui.forms.add-account-transaction-test
  (:require
   [clojure.spec.alpha]
   [dinsro.ui.forms.add-account-transaction :as u.f.add-account-transaction]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AddAccountTransactionForm
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.f.add-account-transaction/AddAccountTransactionForm
    ::ct.fulcro3/initial-state
    (fn [] {})
    ::ct.fulcro3/wrap-root? false}))
