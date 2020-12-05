(ns dinsro.views.home-test
  (:require
   [dinsro.views.home :as v.home]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard HomePage
  {::wsm/card-height 11
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.home/HomePage
    ;; ::ct.fulcro3/wrap-root? false
    ::ct.fulcro3/initial-state (fn [] {:auth-id nil
                                       :page-name :home-page})}))
