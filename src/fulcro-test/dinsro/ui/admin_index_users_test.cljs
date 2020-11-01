(ns dinsro.ui.admin-index-users-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard AdminIndexUsers
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-users/AdminIndexUsers
    ::ct.fulcro3/initial-state
    (fn [] {:users []})
    ::ct.fulcro3/wrap-root? false}))
