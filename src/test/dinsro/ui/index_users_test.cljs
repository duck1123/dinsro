(ns dinsro.ui.index-users-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-users :as u.index-users]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard IndexUsers
  {::wsm/align       {:flex 1}
   ::wsm/card-height 10
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root       u.index-users/IndexUsers
    ::ct.fulcro3/initial-state
    (fn [] {:user/id    sample/user-map
            :users/list (map sample/user-map [1 2])})
    ::ct.fulcro3/wrap-root? false}))
