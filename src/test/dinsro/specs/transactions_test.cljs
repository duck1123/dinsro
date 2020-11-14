(ns dinsro.specs.transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.model.transactions :as m.transactions]
   [expound.alpha :as expound]
   [tick.alpha.api :as tick]))

(let [item {:db/id 1
            ::m.transactions/account {:db/id 1}
            ::m.transactions/date (tick/instant)
            ::m.transactions/description "Foo"
            ::m.transactions/value -3}]
  (defcard-rg item-test-validation
    [:pre (expound/expound-str ::m.transactions/item item)])

  (deftest item-test
    (is (s/valid? ::m.transactions/item item))))
