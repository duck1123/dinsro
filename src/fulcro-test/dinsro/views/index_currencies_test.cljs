(ns dinsro.views.index-currencies-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.views.index-currencies :as v.index-currencies]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexCurrenciesPage
  {::wsm/card-height 10
   ::wsm/card-width 3}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-currencies/IndexCurrenciesPage
    ::ct.fulcro3/initial-state
    (fn [] {:button-data {}
            :form-data {}
            :currencies {:currencies (map sample/currency-map [1 2])}})
    ::ct.fulcro3/wrap-root? false}))
