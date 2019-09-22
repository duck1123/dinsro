(ns dinsro.components.register-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [dinsro.components.register :as register]
            [reagent.core :as r]))

(deftest registration-page-
  (is (vector? (register/registration-page- (r/atom {})))))


#_(deftest registration-page
  (is (vector? ((register/registration-page)))))
