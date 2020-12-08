(ns dinsro.ui.datepicker-test
  (:require
   [dinsro.translations :refer [tr]]
   [dinsro.ui.datepicker :as u.datepicker]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard Datepicker
  {::wsm/card-height 6
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.datepicker/Datepicker
    ::ct.fulcro3/initial-state
    (fn [] {})
    ::ct.fulcro3/wrap-root? false}))
