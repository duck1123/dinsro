(ns dinsro.components.transaction-value-chart
  (:require
   [cljsjs.highcharts]
   [clojure.spec.alpha :as s]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.rates :as s.rates]
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

(defn get-config
  [account data]
  {:chart {:type  :line}
   :title {:text  "Account Value over time"}
   :xAxis {:title {:text "Date"}
           :type "datetime"}
   :yAxis {:title {:text "Sats"}}
   :plotOptions
   {:series {:label {:connectorAllowed false}}}
   :series [{:name (::s.accounts/name account)
             :data data}]})

(defn transaction-value-chart
  [account data]
  (let [currency-id (get-in account [::s.accounts/currency :db/id])]
    (let [config (get-config account data)]
      [:<>
       [:p "Chart"]
       [:p "Currency ID: " currency-id]
       [chart-outer config]])))

(s/fdef transaction-value-chart
  :args (s/cat
         :_account ::s.accounts/item
         :data ::s.rates/rate-feed)
  :ret vector?)
