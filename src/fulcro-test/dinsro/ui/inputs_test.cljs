(ns dinsro.ui.inputs-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard TextInput
  {::wsm/card-height 6
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.inputs/TextInput
    ::ct.fulcro3/initial-state
    (fn [] {})
    ::ct.fulcro3/wrap-root? false}))

(ws/defcard NumberInput
  {::wsm/card-height 6
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.inputs/NumberInput
    ::ct.fulcro3/initial-state
    (fn [] {})
    ::ct.fulcro3/wrap-root? false}))

(ws/defcard AccountSelector
  {::wsm/card-height 6
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.inputs/AccountSelector
    ::ct.fulcro3/initial-state
    (fn []
      {:accounts (map sample/account-map [1 2])})
    ::ct.fulcro3/wrap-root? false}))

(ws/defcard CurrencySelector
  {::wsm/card-height 6
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.inputs/CurrencySelector
    ::ct.fulcro3/initial-state
    (fn [] {:currencies (vals sample/currency-map)})
    ::ct.fulcro3/wrap-root? false}))

(ws/defcard UserSelector
  {::wsm/card-height 6
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.inputs/UserSelector
    ::ct.fulcro3/initial-state
    (fn [] {:users (vals sample/user-map)})
    ::ct.fulcro3/wrap-root? false}))

(ws/defcard PrimaryButton
  {::wsm/card-height 6
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.inputs/PrimaryButton
    ::ct.fulcro3/initial-state
    (fn [] {})
    ::ct.fulcro3/wrap-root? false}))
