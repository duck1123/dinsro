(ns dinsro.specs.accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.model.accounts :as m.accounts]))

(let [item {:db/id 1
            ::m.accounts/name "foo"
            ::m.accounts/initial-value 1
            ::m.accounts/user {:db/id 1}
            ::m.accounts/currency {:db/id 1}}]
  (deftest item-test
    (is (s/valid? ::m.accounts/item item)
        "Valid map")))
