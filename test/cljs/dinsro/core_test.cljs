(ns dinsro.core-test
  (:require [cljs.test :refer-macros [is]]
            [devcards.core :as dc :refer-macros [deftest]]
            [dinsro.core]))

(declare test-home)
(deftest test-home
  (is (= true true)))
