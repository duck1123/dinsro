(ns dinsro.views.index-accounts-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.views.index-accounts :as v.index-accounts]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexAccountsPage
  {::wsm/align {:flex 1}
   ::wsm/card-height 12
   ::wsm/card-width 5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-accounts/IndexAccountsPage
    ::ct.fulcro3/initial-state
    (fn [] {:accounts {:index-data {:accounts (map sample/account-map [1 2])}}})}))
