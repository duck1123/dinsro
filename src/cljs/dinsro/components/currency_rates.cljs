(ns dinsro.components.currency-rates
  (:require [dinsro.components :as c]
            [dinsro.components.forms.add-currency-rate :as c.f.add-currency-rate]
            [dinsro.components.index-rates :as c.index-rates]
            [dinsro.components.rate-chart :as c.rate-chart]
            [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]))

(defn section
  [currency-id rates]
  [:div.box
   [:h2
    "Rates"
    [c/show-form-button ::e.f.add-currency-rate/shown?]]
   [c.f.add-currency-rate/form currency-id]
   [:hr]
   [c.rate-chart/rate-chart (reverse rates)]
   [c.index-rates/section rates]])
