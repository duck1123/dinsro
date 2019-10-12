(ns dinsro.view-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [dinsro.view :as view]))

(deftest about-page-test
  (is (vector? (view/about-page))))

(deftest home-page-test
  (is (vector? (view/home-page))))
