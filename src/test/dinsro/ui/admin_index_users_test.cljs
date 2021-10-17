(ns dinsro.ui.admin-index-users-test
  (:require
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [dinsro.ui.index-users :as u.index-users]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard AdminIndexUsers
  {::wsm/align       {:flex 1}
   ::wsm/card-height 11
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.admin-index-users/AdminIndexUsers
    ::ct.fulcro3/initial-state
    (fn []
      (let [user-id (new-uuid)
            user    {::m.users/id   user-id
                     ::m.users/name m.users/default-username
                     ::m.users/link {::m.users/id   user-id
                                     ::m.users/name m.users/default-username}}]
        {::u.admin-index-users/toggle-button {:form-button/id u.admin-index-users/form-toggle-sm}

         ::u.admin-index-users/users
         {::u.index-users/items [user]}}))}))
