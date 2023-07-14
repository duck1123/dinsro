(ns dinsro.ui.charts
  (:require
   #?(:cljs ["victory" :as victory])
   #?(:cljs [com.fulcrologic.fulcro.algorithms.react-interop :as interop])
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])))

(def ui-victory-chart
  #?(:cljs (interop/react-factory victory/VictoryChart)
     :clj (fn [_this _props])))
(def ui-victory-line
  #?(:cljs (interop/react-factory victory/VictoryLine)
     :clj (fn [_props])))

(defsc RateChart
  [_this {:keys [rates height width]}]
  {:initial-state {:rates  [] :height 50 :width  50}}
  (dom/div {:style {:width  (str width "px") :height (str height "px")}}
    (ui-victory-chart
     {:domainPadding {:x 50}}
     (ui-victory-line
      {:data   rates
       :style  #?(:clj nil
                  :cljs  (clj->js {:data {:stroke "#c43a31"}}))
       :labels (fn [v] (comp/isoget-in v ["date" "rate"]))
       :x      "date"
       :y      "rate"}))))

(def ui-rate-chart (comp/factory RateChart))
