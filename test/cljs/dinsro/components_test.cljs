(ns dinsro.components-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [dinsro.components :as components]))

(deftest about-page-test
  (is (vector? (components/about-page))))

(deftest home-page-test
  (is (vector? (components/home-page))))
