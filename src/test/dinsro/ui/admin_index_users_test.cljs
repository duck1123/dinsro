(ns dinsro.ui.admin-index-users-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [dinsro.ui.index-users :as u.index-users]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(def users (map sample/user-map [1 2]))

(ws/defcard AdminIndexUsers
  {::wsm/align       {:flex 1}
   ::wsm/card-height 11
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-users/AdminIndexUsers
    ::ct.fulcro3/initial-state
    (fn []
      {::u.admin-index-users/users
       {::u.index-users/items users}})}))