(ns dinsro.spec.transactions-test
  (:require [cljs.test :refer-macros [is are testing use-fixtures]]
            [clojure.spec.alpha :as s]
            [devcards.core :as dc :refer-macros [defcard defcard-rg deftest]]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]
            [expound.alpha :as expound]
            [tick.alpha.api :as tick]))

(defcard item
  (ds/gen-key ::s.transactions/item)
  )

(let [item {
            ::s.transactions/account {:db/id 1}
            ::s.transactions/currency {:db/id 1}
            ::s.transactions/date (tick/instant)
            ::s.transactions/description "Foo"
            ::s.transactions/value -3
            }]
  (defcard-rg item-test-validation
    [:pre (expound/expound-str ::s.transactions/item item)])
  (deftest item-test
    (is (s/valid? ::s.transactions/item item))))
