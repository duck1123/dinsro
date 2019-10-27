(ns dinsro.views.index-rates
  (:require [dinsro.components.forms.create-rate :refer [create-rate-form]]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.components.rate-chart :refer [rate-chart]]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn page
  []
  (let [items @(rf/subscribe [::e.rates/items])]
    [:section.section>div.container>div.content
     [:h1 "Rates"]
     [rate-chart items]
     [create-rate-form]
     [index-rates items]]))
