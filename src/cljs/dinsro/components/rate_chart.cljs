(ns dinsro.components.rate-chart
  (:require [cljsjs.highcharts]
            [reagent.core :as r]))

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

(defn rate-chart
  [items]
  (let [config {:chart {:type  :line}
                :title {:text  "Sats per Dollar"}
                :xAxis {:title {:text "Date"}}
                :yAxis {:title {:text "Dollars"}}
                :plotOptions
                {:series {:label {:connectorAllowed false}
                          :pointStart 2010}}
                :series [{:name "Jane" :data (map :value items)}]}]
    [chart-outer config]))
