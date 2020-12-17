(ns dinsro.ui.currency-rates
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.bulma :as bulma]
   [taoensso.timbre :as timbre]))

(defn ui-show-form-button
  [_props])

(defn ui-add-currency-rate-form
  [_props])

(defn ui-rate-chart
  [_props])

(defsc CurrencyRateList
  [_this {:keys [rate-feed]
          currency-id :currency/id}]
  {:initial-state {:currency/id 1
                   :rate-feed []}}
  (bulma/box
   (dom/h2
    "Rates"
    (ui-show-form-button {}))
   (ui-add-currency-rate-form {:currency/id currency-id})
   (dom/hr)
   (ui-rate-chart rate-feed)))
