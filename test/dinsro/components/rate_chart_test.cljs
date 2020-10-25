(ns dinsro.components.rate-chart-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.rate-chart :as c.rate-chart]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.components.rate-chart-test
 "Rate Chart Components" [])

(let [data [[1  1]
            [2  2]
            [3  4]
            [4  8]
            [5 16]]]
  (defcard data data)

  (defcard-rg rate-chart
    (fn []
      [error-boundary
       (c.rate-chart/rate-chart data)])))