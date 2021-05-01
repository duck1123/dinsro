(ns dinsro.ui.show-currency-test
  (:require
   [dinsro.model.currencies :as m.currencies]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-currency :as u.show-currency]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(def currency-map
  {"foo" {:currency/id   "foo"
          :currency/name "foo"}})

(ws/defcard ShowCurrency
  {::wsm/card-height 6
   ::wsm/card-width  2}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.show-currency/ShowCurrency
    ::ct.fulcro3/initial-state
    (fn []
      {::m.currencies/id   "usd"
       ::m.currencies/name "Dollars"})}))
