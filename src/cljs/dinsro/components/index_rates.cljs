(ns dinsro.components.index-rates
  (:require [clojure.spec.alpha :as s]
            [dinsro.specs :as ds]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn rate-line
  [item]
  (let [strings {:delete "Delete"
                 :id "Id: "
                 :value "Value: "}]
    [:div.column
     {:style {:border "1px black solid"}}
     [:p (:id strings) (:id item)]
     [:p (:value strings) (:value item)]
     [:a.button {:on-click #(rf/dispatch [::do-delete-rate item])} (:delete strings)]]))

(defn index-rates
  [items]
  (let [strings {:no-rates "No Rates"}]
    (if-not (seq items)
      [:p (:no-rates strings)]
      (->> (for [item items] ^{:key (:id item)} [rate-line item])
           (into [:div])))))

(s/fdef index-rates
  :args (s/cat :items ::ds/rates))
