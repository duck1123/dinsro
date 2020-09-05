(ns dinsro.spec.accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.cards :as cards]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [expound.alpha :as expound]))

(cards/header "Account Specs" [])

(defcard item
  (ds/gen-key ::s.accounts/item))

(let [item {:db/id 1
            ::s.accounts/name "foo"
            ::s.accounts/initial-value 1
            ::s.accounts/user {:db/id 1}
            ::s.accounts/currency {:db/id 1}}]
  (defcard item-test-validation
    (expound/expound-str ::s.accounts/item item))

  (deftest item-test
    (is (s/valid? ::s.accounts/item item)
        "Valid map")))
