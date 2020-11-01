(ns dinsro.ui.show-currency-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-currency :as u.show-currency]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard ShowCurrency
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-currency/ShowCurrency
    ::ct.fulcro3/initial-state
    (fn [] {})
    ::ct.fulcro3/wrap-root? false}))
