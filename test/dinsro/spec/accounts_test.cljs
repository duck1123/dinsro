(ns dinsro.spec.accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.spec.accounts :as s.accounts]))

(let [item {:db/id 1
            ::s.accounts/name "foo"
            ::s.accounts/initial-value 1
            ::s.accounts/user {:db/id 1}
            ::s.accounts/currency {:db/id 1}}]
  (deftest item-test
    (is (s/valid? ::s.accounts/item item)
        "Valid map")))
