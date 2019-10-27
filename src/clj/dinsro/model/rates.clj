(ns dinsro.model.rates
  (:require [java-time :as jt]))

(def rate {:id 1 :value 12158 :time (jt/instant)})
(def default-rates
  (map (fn [i]
         (-> rate
             (update :value (partial + i))
             (update :id (partial + i))))
       (range 7)))

(defn fetch-index
  []
  default-rates)
