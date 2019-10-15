(ns dinsro.components.index-currencies
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn index-currencies
  []
  (let [currencies [{:id 1} {:id 2}]]
    [:div
     [:p "Index Currencies"]
     (into
      [:div.section]
      (for [{:keys [id] :as currency} currencies]
        ^{:key id}
        [:div.column
         {:style {:border "1px black solid"
                  :margin-bottom "15px"}}
         [:p id]]))]))
