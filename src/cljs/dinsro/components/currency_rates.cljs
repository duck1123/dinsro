(ns dinsro.components.currency-rates
  (:require [dinsro.components.forms.add-currency-rate :as c.f.add-currency-rate]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.components.rate-chart :as c.rate-chart]))

(defn section
  [currency-id rates]
  [:div.box
   [:h2 "Rates"]
   [c.f.add-currency-rate/form currency-id]
   [:hr]
   [c.rate-chart/rate-chart rates]
   [index-rates rates]])
