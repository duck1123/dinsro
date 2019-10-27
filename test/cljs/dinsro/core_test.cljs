(ns dinsro.core-test
  (:require [cljs.test :refer-macros [is are testing use-fixtures]]
            [devcards.core :as dc :refer-macros [deftest]]
            [dinsro.core :as core]
            [pjstadig.humane-test-output]
            [reagent.core :as reagent :refer [atom]]
            ))

(deftest test-home
  (is (= true true)))
