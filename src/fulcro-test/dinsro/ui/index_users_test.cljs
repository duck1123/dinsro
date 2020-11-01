(ns dinsro.ui.index-users-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-users :as u.index-users]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard IndexUsers
  {::wsm/card-height 5
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-users/IndexUsers
    ::ct.fulcro3/initial-state
    (fn [] {:users/list [{:user/id 1
                          :user/name "Foo"}
                         {:user/id 2
                          :user/email "bard"}]})
    ::ct.fulcro3/wrap-root? false}))
