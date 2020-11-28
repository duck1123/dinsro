(ns dinsro.views.index-rate-sources-test
  (:require
   [dinsro.views.index-rate-sources :as v.index-rate-sources]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexRateSourcesPage
  {::wsm/card-height 8
   ::wsm/card-width 2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-rate-sources/IndexRateSourcesPage
    ::ct.fulcro3/initial-state
    (fn [] {})

    ::ct.fulcro3/wrap-root? false}))
