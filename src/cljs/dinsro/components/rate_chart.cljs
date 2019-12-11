(ns dinsro.components.rate-chart
  (:require [cljsjs.highcharts]
            [dinsro.spec.rates :as s.rates]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

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
                :title {:text  "Sats Exchange Rate"}
                :xAxis {:title {:text "Date"}}
                :yAxis {:title {:text "Sats"}}
                :plotOptions
                {:series {:label {:connectorAllowed false}}}
                :series [{:name "USD"
                          :data (map
                                 (fn [item] [(.getTime (::s.rates/date item)) (::s.rates/rate item)])
                                 items)}]}]
    [chart-outer config]))
