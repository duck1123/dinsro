(ns dinsro.views.index-rates-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.views.index-rates :as v.index-rates]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexRatesPage
  {::wsm/align {:flex 1}
   ::wsm/card-height 11
   ::wsm/card-width 5}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-rates/IndexRatesPage
    ::ct.fulcro3/initial-state
    (fn [] {:button-data {}
            :form-data {}
            :rates {:rates/items (vals sample/rate-map)}})}))
