(ns dinsro.specs.rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.model.rates :as m.rates]
   [tick.alpha.api :as tick]))

(let [item {:db/id 1
            ::m.rates/name "foo"
            ::m.rates/rate 1.0
            ::m.rates/date (tick/instant)
            ::m.rates/currency {:db/id 1}}]
  (deftest item-test
    (is (s/valid? ::m.rates/item item)
        "Valid map")))
