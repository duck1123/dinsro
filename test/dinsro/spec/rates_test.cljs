(ns dinsro.spec.rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.spec.rates :as s.rates]
   [tick.alpha.api :as tick]))

(let [item {:db/id 1
            ::s.rates/name "foo"
            ::s.rates/rate 1.0
            ::s.rates/date (tick/instant)
            ::s.rates/currency {:db/id 1}}]
  (deftest item-test
    (is (s/valid? ::s.rates/item item)
        "Valid map")))
