(ns dinsro.ui.charts
  (:require
   ["victory" :as victory]
   [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]))

(def ui-victory-chart (interop/react-factory victory/VictoryChart))
(def ui-victory-line (interop/react-factory victory/VictoryLine))

(defsc RateChart
  [_this {:keys [rates height width]}]
  {:initial-state {:rates  [] :height 50 :width  50}}
  (dom/div {:style {:width  (str width "px") :height (str height "px")}}
    (ui-victory-chart
     {:domainPadding {:x 50}}
     (ui-victory-line
      {:data   rates
       :style  (clj->js {:data {:stroke "#c43a31"}})
       :labels (fn [v] (comp/isoget-in v ["date" "rate"]))
       :x      "date"
       :y      "rate"}))))

(def ui-rate-chart (comp/factory RateChart))
