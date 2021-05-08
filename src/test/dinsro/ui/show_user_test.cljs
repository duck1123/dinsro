(ns dinsro.ui.show-user-test
  (:require
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-user :as u.show-user]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard ShowUser
  {::wsm/card-height 7
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-user/ShowUser
    ::ct.fulcro3/initial-state
    (fn []
      {::m.users/id "admin"})}))
