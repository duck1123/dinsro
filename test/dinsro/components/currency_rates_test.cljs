(ns dinsro.components.currency-rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.currency-rates :as c.currency-rates]))

(let [rates [[1 1]
             [2 2]
             [3 4]]]

  (defcard-rg c.currency-rates/section
    [c.currency-rates/section rates])

  (deftest section-test
    (is (vector? (c.currency-rates/section rates)))))
