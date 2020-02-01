(ns dinsro.components.rate-chart-test
  (:require
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.components.rate-chart :as c.rate-chart]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(let [data [[1  1]
            [2  2]
            [3  4]
            [4  8]
            [5 16]]]
  (defcard data data)

  (defcard-rg rate-chart
    [c.rate-chart/rate-chart data]))
