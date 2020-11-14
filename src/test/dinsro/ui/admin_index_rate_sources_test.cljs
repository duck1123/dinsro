(ns dinsro.ui.admin-index-rate-sources-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.specs :as ds]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.specs.rate-sources :as s.rate-sources]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-rate-sources :as u.admin-index-rate-sources]))

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

  (let [store (test-store)]
    (st/dispatch store [::e.rate-sources/do-fetch-index-success {:items rate-sources}])

    (defcard-rg index-line
      [:table.table>tbody
       [u.admin-index-rate-sources/index-line store rate-source]])

    (deftest index-line-test
      (is (vector? (u.admin-index-rate-sources/index-line store rate-source)))))

  (let [store (test-store)]
    (st/dispatch store [::e.rate-sources/do-fetch-index-success {:items rate-sources}])

    (defcard-rg rate-sources-table
      [u.admin-index-rate-sources/rate-sources-table store rate-sources])

    (deftest rate-sources-table-test
      (is (vector? (u.admin-index-rate-sources/rate-sources-table store rate-sources)))))

  (let [store (test-store)]
    (st/dispatch store [::e.rate-sources/do-fetch-index-success {:items rate-sources}])

    (defcard-rg section
      [u.admin-index-rate-sources/section store])

    (deftest section-test
      (is (vector? (u.admin-index-rate-sources/section store))))))
