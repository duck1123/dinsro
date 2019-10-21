(ns dinsro.components.index-rates)

(defn index-rates
  [items]
  (if-not (seq items)
    [:p "No Rates"]
    [:div
     [:p "Rates"]
     (->> (for [item items]
            ^{:key (:id item)}
            [:div
             [:p "id: " (:id item)]
             [:p "Value: " (:value item)]])
          (into [:div.section]))]))
