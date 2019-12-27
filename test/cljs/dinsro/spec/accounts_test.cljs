(ns dinsro.spec.accounts-test
  (:require [cljs.test :refer-macros [is are testing use-fixtures]]
            [clojure.spec.alpha :as s]
            [devcards.core :as dc :refer-macros [defcard deftest]]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.specs :as ds]
            [expound.alpha :as expound]))

(defcard item
  (ds/gen-key ::s.accounts/item))

(let [item {::s.accounts/name "foo"
            ::s.accounts/initial-value 1
            ::s.accounts/user {:db/id 1}
            ::s.accounts/currency {:db/id 1}}]
  (defcard item-test-validation
    (expound/expound-str ::s.accounts/item item))
  (deftest item-test
    (is (s/valid? ::s.accounts/item item))))
