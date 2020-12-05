(ns dinsro.ui.index-rates-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.model.rates :as m.rates]
   [dinsro.specs :as ds]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-rates :as u.index-rates]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.currencies/init-handlers!
                e.debug/init-handlers!)]
    store))

(let [items (ds/gen-key (s/coll-of ::m.rates/item :count 3))]

  (let [item (first items)
        store (test-store)]
    (defcard-rg rate-line
      [:table.table>tbody
       [u.index-rates/rate-line store item]])

    (deftest rate-line-test
      (is (vector (u.index-rates/rate-line store item)))))

  (let [store (test-store)]
    (defcard-rg section
      [u.index-rates/section store items])

    (deftest section-test
      (is (vector? (u.index-rates/section store items))))))
