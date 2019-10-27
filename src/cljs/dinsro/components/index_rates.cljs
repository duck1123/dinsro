(ns dinsro.components.index-rates
  (:require [clojure.spec.alpha :as s]
            [cljsjs.highcharts]
            [dinsro.specs :as ds]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn mount-chart [comp]
  (.chart js/Highcharts (r/dom-node comp) (clj->js (r/props comp))))

(defn update-chart [comp]
  (mount-chart comp))

(defn chart-inner []
  (r/create-class
   {:component-did-mount   mount-chart
    :component-did-update  update-chart
    :reagent-render        (fn [comp] [:div])}))

(defn chart-outer [config]
  [chart-inner config])

(defn index-rates
  [items]
  (let [strings {:delete "Delete"
                 :header "Rates"
                 :id "Id: "
                 :no-rates "No Rates"
                 :value "Value: "}
        config {:chart {:type  :line}
                :title {:text  "Sats per Dollar"}
                :xAxis {:title {:text "Date"}}
                :yAxis {:title {:text "Dollars"}}
                :plotOptions
                {:series {:label {:connectorAllowed false}
                          :pointStart 2010}}
                :series [{:name "Jane" :data (map :value items)}]}]
    (if-not (seq items)
      [:p (:no-rates strings)]
      [:div
       [:p (:header strings)]
       [chart-outer config]
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
