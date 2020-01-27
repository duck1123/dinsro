(ns dinsro.views.index-accounts-test
  (:require
   [clojure.spec.alpha :as s]
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.views.index-accounts :as v.index-accounts]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(let [items (ds/gen-key (s/coll-of ::s.accounts/item :count 5))
      template nil
      data nil
      result nil
      path "/"
      path-params {}
      match (rc/->Match template data result path-params path)]

  (defcard items items)

  (deftest page
    (is (vector? (v.index-accounts/page match))))

  (defcard-rg page-card
    [v.index-accounts/page match]))
