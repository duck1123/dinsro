(ns dinsro.views.index-transactions-test
  (:require [clojure.spec.alpha :as s]
            [cljs.test :refer-macros [is]]
            [devcards.core :refer-macros [defcard-rg deftest]]
            [dinsro.spec :as ds]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.views.index-transactions :as v.index-transactions]
            [taoensso.timbre :as timbre]))

(let [items (ds/gen-key (s/coll-of ::s.transactions/items :count 5))]

  (defcard-rg v.index-transactions/section-inner
    [v.index-transactions/section-inner items])

  (declare page)
  (deftest page
    (is (vector? (v.index-transactions/page)))))

(defcard-rg page
  [v.index-transactions/page])
