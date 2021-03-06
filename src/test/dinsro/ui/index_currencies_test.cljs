(ns dinsro.ui.index-currencies-test
  (:require
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-currencies :as u.index-currencies]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(ws/defcard IndexCurrencies
  {::wsm/card-height 7
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.index-currencies/IndexCurrencies
    ::ct.fulcro3/initial-state
    (fn []
      {::u.index-currencies/currencies
       [{::m.currencies/id "sats"
         ::m.currencies/link
         [{::m.currencies/id   "sats"
           ::m.currencies/name "Sats"}]}]})}))
