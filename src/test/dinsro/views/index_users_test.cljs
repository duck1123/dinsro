(ns dinsro.views.index-users-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.views.index-users :as v.index-users]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(ws/defcard IndexUsersPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 13
   ::wsm/card-width  5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-users/IndexUsersPage
    ::ct.fulcro3/initial-state
    (fn []
      {::v.index-users/users {:users/list (map sample/user-map [1 2])}})}))
