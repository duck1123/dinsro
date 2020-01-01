(ns dinsro.spec.accounts-test
  (:require [cljs.test :refer-macros [is]]
            [clojure.spec.alpha :as s]
            [devcards.core :as dc :refer-macros [defcard deftest]]
            [dinsro.spec :as ds]
            [dinsro.spec.accounts :as s.accounts]
            [expound.alpha :as expound]))

(declare item)
(defcard item
  (ds/gen-key ::s.accounts/item))

(let [item {::s.accounts/name "foo"
            ::s.accounts/initial-value 1
            ::s.accounts/user {:db/id 1}
            ::s.accounts/currency {:db/id 1}}]
  (declare item-test-validation)
  (defcard item-test-validation
    (expound/expound-str ::s.accounts/item item))

  (declare item-test)
  (deftest item-test
    (is (s/valid? ::s.accounts/item item)
        "Valid map")))
