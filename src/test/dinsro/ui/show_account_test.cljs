(ns dinsro.ui.show-account-test
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-account :as u.show-account]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard ShowAccount
  {::wsm/card-height 8
   ::wsm/card-width  3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-account/ShowAccount
    ::ct.fulcro3/initial-state
    (fn []
      {::m.accounts/name     "Savings"
       ::m.accounts/currency [{::m.currencies/id "sats" ::m.currencies/name "Sats"}]
       ::m.accounts/user     [{::m.users/id m.users/default-username}]})}))
