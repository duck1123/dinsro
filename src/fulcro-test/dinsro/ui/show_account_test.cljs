(ns dinsro.ui.show-account-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-account :as u.show-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard ShowAccount
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-account/ShowAccount
    ::ct.fulcro3/initial-state
    (fn [] {::u.show-account/name "Test Name"
            ::u.show-account/currency-id 7
            ::u.show-account/user-id 4})
    ::ct.fulcro3/wrap-root? false}))
