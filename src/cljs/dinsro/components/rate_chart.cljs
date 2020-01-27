(ns dinsro.components.rate-chart
  (:require
   [cljsjs.highcharts]
   [reagent.core :as r]))

(defn mount-chart [comp]
  (.chart js/Highcharts (r/dom-node comp) (clj->js (r/props comp))))

(defn update-chart [comp]
  (mount-chart comp))

(defn chart-inner []
  (r/create-class
   {:component-did-mount   mount-chart
    :component-did-update  update-chart
    :reagent-render        (fn [_] [:div])}))

(defn chart-outer [config]
  [chart-inner config])

(defn rate-chart
  [data]
  (let [config {:chart {:type  :line}
                :title {:text  "Sats Exchange Rate"}
                :xAxis {:title {:text "Date"}
                        :type "datetime"}
                :yAxis {:title {:text "Sats"}}
                :plotOptions
                {:series {:label {:connectorAllowed false}}}
                :series [{:name "USD"
                          :data data}]}]
    [chart-outer config]))
