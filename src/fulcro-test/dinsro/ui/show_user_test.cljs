(ns dinsro.ui.show-user-test
  (:require
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-user :as u.show-user]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard ShowUser
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-user/ShowUser
    ::ct.fulcro3/initial-state
    (fn [] {::u.show-user/name (ds/gen-key ::m.users/name)
            ::u.show-user/email (ds/gen-key ::m.users/email)})
    ::ct.fulcro3/wrap-root? false}))
