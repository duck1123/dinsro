(ns dinsro.core-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.core]))

(deftest test-home
  (is (= true true)))
