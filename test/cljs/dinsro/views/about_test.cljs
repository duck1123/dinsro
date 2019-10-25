(ns dinsro.views.about-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [dinsro.views.about :as about]
            [pjstadig.humane-test-output]))

(deftest about-page-test
  (is (vector? (about/page))))
