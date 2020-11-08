(ns dinsro.ui.rate-chart
  (:require
   [cljsjs.highcharts]
   [clojure.spec.alpha :as s]
   [dinsro.model.rates :as m.rates]
   [reagent.core :as r]
   [reagent.dom :as dom]))

(defn mount-chart [comp]
  (.chart js/Highcharts (dom/dom-node comp) (clj->js (r/props comp))))

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
  [rate-feed]
  (let [config {:chart {:type  :line}
                :title {:text  "Sats Exchange Rate"}
                :xAxis {:title {:text "Date"}
                        :type "datetime"}
                :yAxis {:title {:text "Sats"}}
                :plotOptions
                {:series {:label {:connectorAllowed false}}}
                :series [{:name "USD"
                          :data rate-feed}]}]
    [chart-outer config]))

(s/fdef rate-chart
  :args (s/cat :rate-feed ::m.rates/rate-feed)
  :ret vector?)
