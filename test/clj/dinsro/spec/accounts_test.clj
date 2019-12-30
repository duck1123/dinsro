(ns dinsro.spec.accounts-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [clojure.test.check.generators]
            [dinsro.spec.accounts :as s.accounts]
            [expound.alpha :as expound]
            [taoensso.timbre :as timbre]))

(def valid-item
  {:db/id 1
   ::s.accounts/name "Foo"
   ::s.accounts/initial-value 2.3
   ::s.accounts/currency {:db/id 1}
   ::s.accounts/user {:db/id 1}})

(deftest item-spec-test
  (is (s/valid? ::s.accounts/item valid-item)
      "Valid map"))
