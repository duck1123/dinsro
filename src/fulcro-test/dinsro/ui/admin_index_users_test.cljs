(ns dinsro.ui.admin-index-users-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard AdminIndexUsers
  {::wsm/align       {:flex 1}
   ::wsm/card-height 11
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-users/AdminIndexUsers
    ::ct.fulcro3/initial-state
    (fn [] {:users {:users/list (map sample/user-map [1])}})
    ::ct.fulcro3/wrap-root? false}))
