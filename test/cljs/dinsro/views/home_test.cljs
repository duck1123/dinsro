(ns dinsro.views.home-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [dinsro.views.home :as home]
            [pjstadig.humane-test-output]))

(deftest page-test
  (let [result (home/page)]
    (is (vector? result))))
