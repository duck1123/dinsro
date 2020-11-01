(ns dinsro.views.home-test
  (:require
   [dinsro.views.home :as v.home]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(ws/defcard HomePage
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.home/HomePage
    ::ct.fulcro3/wrap-root? false}))
