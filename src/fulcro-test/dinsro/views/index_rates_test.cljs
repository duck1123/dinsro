(ns dinsro.views.index-rates-test
  (:require
   [dinsro.views.index-rates :as v.index-rates]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexRatesPage
  {::wsm/card-height 8
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-rates/IndexRatesPage
    ::ct.fulcro3/initial-state
    (fn [] {})

    ::ct.fulcro3/wrap-root? false}))
