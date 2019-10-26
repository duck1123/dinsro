(ns dinsro.components.index-rates
  (:require [clojure.spec.alpha :as s]
            [dinsro.specs :as ds]
            [re-frame.core :as rf]))

(defn index-rates
  [items]
  (let [strings {:delete "Delete"
                 :header "Rates"
                 :id "Id: "
                 :no-rates "No Rates"
                 :value "Value: "}]
    (if-not (seq items)
      [:p (:no-rates strings)]
      [:div
       [:p (:header strings)]
       (->> (for [item items]
              ^{:key (:id item)}
              [:div.column
               {:style {:border "1px black solid"
                        :margin-bottom "15px"}}
               [:p (:id strings) (:id item)]
               [:p (:value strings) (:value item)]
               [:a.button {:on-click #(rf/dispatch [::do-delete-rate item])} (:delete strings)]])
            (into [:div.section]))])))

(s/fdef index-rates
  :args (s/cat :items ::ds/rates))
