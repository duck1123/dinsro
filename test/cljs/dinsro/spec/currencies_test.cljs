(ns dinsro.spec.currencies-test
  (:require [cljs.test :refer-macros [is are testing use-fixtures]]
            [clojure.spec.alpha :as s]
            [devcards.core :as dc :refer-macros [defcard deftest]]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.specs :as ds]
            [expound.alpha :as expound]))

(defcard item
  (ds/gen-key ::s.currencies/item))

(let [item {::s.currencies/name "foo"}]
  (defcard item-test-validation
    (expound/expound-str ::s.currencies/item item))
  (deftest item-test
    (is (s/valid? ::s.currencies/item item))))
