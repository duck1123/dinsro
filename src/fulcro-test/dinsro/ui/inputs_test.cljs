(ns dinsro.ui.inputs-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.inputs :as u.inputs]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard AccountSelector
  {::wsm/card-height 6
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.inputs/AccountSelector
    ::ct.fulcro3/initial-state
    (fn []
      {:accounts (map sample/user-map [1 2])})
    ::ct.fulcro3/wrap-root? false}))
