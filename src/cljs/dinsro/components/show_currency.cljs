(ns dinsro.components.show-currency
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components.forms.add-currency-rate :refer [add-currency-rate-form]]
            [dinsro.components.index-rates :refer [index-rates]]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn-spec show-currency vector?
  [currency ::s.currencies/item rates (s/coll-of ::s.rates/item)]
  [:div
   [:pre (str currency)]
   [:p "Name:" (::s.currencies/name currency)]
   [:a.button {:on-click #(rf/dispatch [::e.rates/do-fetch-index])} "Load"]
   [add-currency-rate-form]
   [index-rates rates]])
