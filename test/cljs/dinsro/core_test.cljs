(ns dinsro.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            [dinsro.core :as core]))

(deftest test-home
  (is (= true true)))

(deftest paths
  ;; TODO: not good tests
  (is (= (core/home-path) "/"))
  (is (= (core/about-path) "/about")))
