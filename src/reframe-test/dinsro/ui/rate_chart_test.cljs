(ns dinsro.ui.rate-chart-test
  (:require
   [dinsro.cards :refer-macros [defcard-rg]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.boundary]
   [dinsro.ui.rate-chart :as u.rate-chart]
   [taoensso.timbre :as timbre]))

(let [data [[1  1]
            [2  2]
            [3  4]
            [4  8]
            [5 16]]]
  (defcard-rg rate-chart
    (u.rate-chart/rate-chart data)))
