(ns dinsro.spec.currencies-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.spec.currencies :as s.currencies]))

(let [item {:db/id 1
            ::s.currencies/name "foo"}]
  (deftest item-test
    (is (s/valid? ::s.currencies/item item))))
