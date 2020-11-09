(ns dinsro.ui.currency-rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.ui.boundary]
   [dinsro.ui.currency-rates :as u.currency-rates]))

(let [rates [[1 1]
             [2 2]
             [3 4]]]

  (defcard-rg section
    [u.currency-rates/section rates])

  (deftest section-test
    (is (vector? (u.currency-rates/section rates)))))
