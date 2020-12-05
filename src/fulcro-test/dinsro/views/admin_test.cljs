(ns dinsro.views.admin-test
  (:require
   [dinsro.views.admin :as v.admin]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard AdminPage
  {::wsm/card-height 11
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.admin/AdminPage
    ::ct.fulcro3/initial-state
    (fn [] {})}))
