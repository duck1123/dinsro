(ns dinsro.core-test
  (:require
   [cljs.test :refer-macros [is]]
   [devcards.core :as dc :refer-macros [deftest]]
   [dinsro.core]))

(deftest test-home
  (is (= true true)))
