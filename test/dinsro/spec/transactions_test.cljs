(ns dinsro.specs.transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.specs.transactions :as s.transactions]
   [expound.alpha :as expound]
   [tick.alpha.api :as tick]))

(let [item {:db/id 1
            ::s.transactions/account {:db/id 1}
            ::s.transactions/date (tick/instant)
            ::s.transactions/description "Foo"
            ::s.transactions/value -3}]
  (defcard-rg item-test-validation
    [:pre (expound/expound-str ::s.transactions/item item)])

  (deftest item-test
    (is (s/valid? ::s.transactions/item item))))
