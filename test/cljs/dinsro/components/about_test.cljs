(ns dinsro.components.about-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [dinsro.components.about :as about]))

(deftest about-page-test
  (is (vector? (about/page))))
