(ns dinsro.ui.user-accounts-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.ui.user-accounts :as u.user-accounts]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexAccounts
  {::wsm/align       {:flex 1}
   ::wsm/card-height 7
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.user-accounts/IndexAccounts
    ::ct.fulcro3/initial-state
    (fn [] {:accounts (map
                       (fn [m] (assoc m :button-data {}))
                       (vals sample/account-map))})
    ::ct.fulcro3/wrap-root? false}))

(ws/defcard UserAccounts
  {::wsm/align       {:flex 1}
   ::wsm/card-height 7
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.user-accounts/UserAccounts
    ::ct.fulcro3/initial-state
    (fn [] {:index-data {:accounts (map sample/account-map [1 2])}})
    ::ct.fulcro3/wrap-root? false}))
