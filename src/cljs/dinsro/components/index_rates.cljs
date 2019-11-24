(ns dinsro.components.index-rates
  (:require [clojure.spec.alpha :as s]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.rates :as s.rates]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(def rate-line-strings
  {:delete "Delete"
   :id "Id: "
   :value "Value: "})

(defn-spec rate-line vector?
  [item ::s.rates/item]
  (let [strings rate-line-strings
        id (:db/id item)
        value (::s.rates/value item)
        currency-id (get-in item [::s.rates/currency :db/id])]
    [:div.column
     {:style {:border "1px black solid"}}
     [:pre (str item)]
     [:p (:id strings) id]
     [:p (:value strings) value]
     [:p "Currency: " [:a {:href (kf/path-for [:show-currency-page {:id currency-id}])} currency-id]]
     [:a.button {:on-click #(rf/dispatch [::e.rates/do-delete-record item])} (:delete strings)]]))

(defn index-rates
  [items]
  (let [strings {:no-rates "No Rates"}]
    (if-not (seq items)
      [:p (:no-rates strings)]
      (->> (for [item items] ^{:key (:db/id item)} [rate-line item])
           (into [:div])))))
