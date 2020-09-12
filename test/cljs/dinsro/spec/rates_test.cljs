(ns dinsro.spec.rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard deftest]]
   [dinsro.cards :as cards]
   [dinsro.spec :as ds]
   [dinsro.spec.rates :as s.rates]
   [expound.alpha :as expound]
   [tick.alpha.api :as tick]))

(cards/header
 'dinsro.spec.rates-test
 "Rate Specs" [])

(defcard item
  (ds/gen-key ::s.rates/item))

(defcard rate-feed-item
  (ds/gen-key ::s.rates/rate-feed-item))

(defcard rate-feed
  (ds/gen-key ::s.rates/rate-feed))

(let [item {:db/id 1
            ::s.rates/name "foo"
            ::s.rates/rate 1.0
            ::s.rates/date (tick/instant)
            ::s.rates/currency {:db/id 1}}]
  (defcard item-test-validation
    (expound/expound-str ::s.rates/item item))

  (deftest item-test
    (is (s/valid? ::s.rates/item item)
        "Valid map")))
