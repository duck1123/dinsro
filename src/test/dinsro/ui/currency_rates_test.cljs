(ns dinsro.ui.currency-rates-test
  (:require
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.currency-rates :as u.currency-rates]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn get-state
  []
  {::u.currency-rates/currency-rates
   {::u.currency-rates/items (vals sample/rate-map)}
   ::u.currency-rates/form          {}
   ::u.currency-rates/toggle-button {}})

(ws/defcard CurrencyRates
  {::wsm/card-height 8
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.currency-rates/CurrencyRates
    ::ct.fulcro3/initial-state get-state}))
