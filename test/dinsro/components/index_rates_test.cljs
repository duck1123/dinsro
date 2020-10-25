(ns dinsro.components.index-rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.index-rates :as c.index-rates]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.spec :as ds]
   [dinsro.spec.rates :as s.rates]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(cards/header
 'dinsro.components.index-rates-test
 "Index Rates Components" [])

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.currencies/init-handlers!
                e.debug/init-handlers!)]
    store))

(let [items (ds/gen-key (s/coll-of ::s.rates/item :count 3))]

  (let [item (first items)
        store (test-store)]
    (comment (defcard item item))

    (defcard-rg rate-line
      (fn []
        [error-boundary
         [:table.table>tbody
          [c.index-rates/rate-line store item]]]))

    (deftest rate-line-test
      (is (vector (c.index-rates/rate-line store item)))))

  (let [store (test-store)]
    (comment (defcard items items))

    (defcard-rg section
      (fn []
        [error-boundary
         [c.index-rates/section store items]]))

    (deftest section-test
      (is (vector? (c.index-rates/section store items))))))
