(ns dinsro.ui.admin-index-accounts-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AdminIndexAccounts
  {::wsm/align {:flex 1}
   ::wsm/card-height 15
   ::wsm/card-width 5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-accounts/AdminIndexAccounts
    ::ct.fulcro3/initial-state
    (fn [] {:accounts (map sample/account-map [1 2])
            :form-data {}})
    ::ct.fulcro3/wrap-root? false}))
