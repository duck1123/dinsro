(ns dinsro.components.admin-index-rate-sources-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.admin-index-rate-sources :as c.admin-index-rate-sources]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.spec :as ds]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.spec.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(cards/header
 'dinsro.components.admin-index-rate-sources-test
 "Admin Index Rate Source Components"
 [#{:rates} #{:components}])

(defn test-store
  []
  (let [store (doto (mock-store)
                e.currencies/init-handlers!
                e.debug/init-handlers!
                e.rate-sources/init-handlers!)]

    (comment (st/dispatch store [::e.debug/set-shown? true]))

    store))

(let [rate-sources (ds/gen-key (s/coll-of ::s.rate-sources/item :count 3))
      rate-source (first rate-sources)]

  (comment (defcard rate-sources rate-sources))

  (let [store (test-store)]
    (st/dispatch store [::e.rate-sources/do-fetch-index-success {:items rate-sources}])

    (defcard-rg c.admin-index-rate-sources/index-line
      (fn []
        [error-boundary
         [:table.table>tbody
          [c.admin-index-rate-sources/index-line store rate-source]]]))

    (deftest index-line-test
      (is (vector? (c.admin-index-rate-sources/index-line store rate-source)))))

  (let [store (test-store)]
    (st/dispatch store [::e.rate-sources/do-fetch-index-success {:items rate-sources}])

    (defcard-rg c.admin-index-rate-sources/rate-sources-table
      (fn []
        [error-boundary
         [c.admin-index-rate-sources/rate-sources-table store rate-sources]]))

    (deftest rate-sources-table-test
      (is (vector? (c.admin-index-rate-sources/rate-sources-table store rate-sources)))))

  (let [store (test-store)]
    (st/dispatch store [::e.rate-sources/do-fetch-index-success {:items rate-sources}])

    (defcard-rg c.admin-index-rate-sources/section
      (fn []
        [error-boundary
         [c.admin-index-rate-sources/section store]]))

    (deftest section-test
      (is (vector? (c.admin-index-rate-sources/section store))))))