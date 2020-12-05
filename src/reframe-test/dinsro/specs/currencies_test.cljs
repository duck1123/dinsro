(ns dinsro.specs.currencies-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.model.currencies :as m.currencies]))

(let [item {:db/id 1
            ::m.currencies/name "foo"}]
  (deftest item-test
    (is (s/valid? ::m.currencies/item item))))
