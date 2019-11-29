(ns dinsro.view
  (:require [dinsro.components.navbar :refer [navbar]]
            [dinsro.routing :as r]
            [kee-frame.core :as kf]
            [taoensso.timbre :as timbre]))

(defn root-component []
  [:div
   [navbar]
   (->> (-> (->> r/mappings
                 (map identity)
                 (into [])
                 (reduce concat []))
            (concat [nil [:div "Not Found"]]))
        (into [kf/switch-route (fn [route] (get-in route [:data :name]))]))])
