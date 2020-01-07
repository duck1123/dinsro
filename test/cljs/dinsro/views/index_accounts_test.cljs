(ns dinsro.views.index-accounts-test
  (:require [clojure.spec.alpha :as s]
            [cljs.test :refer-macros [is]]
            [devcards.core :refer-macros [defcard defcard-rg deftest]]
            [dinsro.spec :as ds]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.views.index-accounts :as v.index-accounts]
            [taoensso.timbre :as timbre]))

(let [items (ds/gen-key (s/coll-of ::s.accounts/item :count 5))]

  (defcard items items)

  (declare page)
  (deftest page
    (is (vector? (v.index-accounts/page nil)))))

(defcard-rg page
  [v.index-accounts/page])
