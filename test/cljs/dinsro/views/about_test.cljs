(ns dinsro.views.about-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [pjstadig.humane-test-output]
            [dinsro.views.about :as about]))

(deftest about-page-test
  (is (vector? (about/page))))
