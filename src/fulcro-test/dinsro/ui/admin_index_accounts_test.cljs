(ns dinsro.ui.admin-index-accounts-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard AdminIndexAccounts
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-accounts/AdminIndexAccounts
    ::ct.fulcro3/initial-state
    (fn [] {:accounts [{:account/name "foo"
                        :account/user-id 1
                        :account/initial-value 0}
                       {:account/name "bar"
                        :account/currency-id 2}]})
    ::ct.fulcro3/wrap-root? false}))
