(ns dinsro.model.rates
  (:require [java-time :as jt]
            [dinsro.db.core :as db]
            [taoensso.timbre :as timbre]))

(def rate {:id 1 :value 12158 :time (jt/instant)})
(def default-rates
  (map (fn [i]
         (-> rate
             (update :value (partial + i))
             (update :id (partial + i))))
       (range 7)))

(defn fetch-index
  []
  #_default-rates
  #_(db/list-rates)
  nil)
