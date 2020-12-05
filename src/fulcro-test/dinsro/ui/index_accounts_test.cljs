(ns dinsro.ui.index-accounts-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-accounts :as u.index-accounts]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard IndexAccounts
  {::wsm/card-height 6
   ::wsm/card-width 4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-accounts/IndexAccounts
    ::ct.fulcro3/initial-state
    (fn []
      {:accounts [{:name "Foo" :user "bob" :currency "sats" :initial-value 1}
                  {:name "Bar" :user "bob" :currency "sats" :initial-value 2}]})
    ::ct.fulcro3/wrap-root? false}))
