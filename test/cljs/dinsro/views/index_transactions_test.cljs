(ns dinsro.views.index-transactions-test
  (:require
   [clojure.spec.alpha :as s]
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.spec :as ds]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.index-transactions :as v.index-transactions]
   [taoensso.timbre :as timbre]))

(cards/header "Index Transactions View" [])

(let [items (ds/gen-key (s/coll-of ::s.transactions/item :count 5))]

  (defcard-rg v.index-transactions/section-inner
    (fn []
      [error-boundary
       [v.index-transactions/section-inner items]]))

  (comment
    (let [store (mock-store)
          match nil]
      (deftest page
        (is (vector? (v.index-transactions/page store match))))))

  (let [store (mock-store)
        match nil]
    (defcard-rg page-card
      (fn []
        [error-boundary
         [v.index-transactions/page store match]]))))
