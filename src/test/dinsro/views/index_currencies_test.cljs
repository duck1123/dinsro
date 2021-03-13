(ns dinsro.views.index-currencies-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.views.index-currencies :as v.index-currencies]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as timbre]))

(ws/defcard IndexCurrenciesPage
  {::wsm/align       {:flex 1}
   ::wsm/card-height 8
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root v.index-currencies/IndexCurrenciesPage
    ::ct.fulcro3/initial-state
    (fn []
      {::v.index-currencies/currencies    {:currencies (map sample/currency-map [1 2])}
       ::v.index-currencies/form          {}
       ::v.index-currencies/toggle-button {}})}))
