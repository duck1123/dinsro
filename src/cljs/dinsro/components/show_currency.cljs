(ns dinsro.components.show-currency
  (:require [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [re-frame.core :as rf]))

(defn show-currency
  [currency]
  (let [rates @(rf/subscribe [::e.rates/items-by-currency currency])]
    [:div
     [:pre (str currency)]
     [:p "Name:" (::s.currencies/name currency)]
     [:a.button {:on-click #(rf/dispatch [::e.rates/do-fetch-index])} "Load"]
     [index-rates rates]]))
