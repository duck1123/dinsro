(ns dinsro.components.show-currency
  (:require [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.events.rates :as e.rates]
            [re-frame.core :as rf]))

(defn show-currency
  [currency]
  (let [rates @(rf/subscribe [::e.rates/items])]
    [:div
     [:p (str currency)]
     [:p "Name:" (:name currency)]
     [:a.button {:on-click #(rf/dispatch [::e.rates/do-fetch-index])} "Load"]
     [index-rates rates]]))
