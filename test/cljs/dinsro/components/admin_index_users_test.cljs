(ns dinsro.components.admin-index-users-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.admin-index-categories :as c.admin-index-categories]
   [dinsro.components.admin-index-users :as c.admin-index-users]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.users :as e.users]
   [dinsro.spec :as ds]
   [dinsro.spec.users :as s.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(cards/header
 'dinsro.components.admin-index-users-test
 "Admin Index Users Components" [])

(defn test-store
  []
  (let [store (doto (mock-store)
                e.debug/init-handlers!
                e.users/init-handlers!)]
    store))

(let [items (ds/gen-key (s/coll-of ::s.users/item :count 3))
      item (first items)]
  (defcard items items)
  (defcard item item)

  (let [store (test-store)]
    (st/dispatch store [::e.users/do-fetch-index-success {:items items}])

    (defcard-rg c.admin-index-categories/category-line
      (fn []
        [error-boundary
         [:table.table>tbody
          [c.admin-index-categories/category-line store item]]]))

    (deftest category-line-test
      (is (vector? [c.admin-index-categories/category-line store item]))))

  (let [store (test-store)]
    (st/dispatch store [::e.users/do-fetch-index-success {:items items}])

    (defcard-rg c.admin-index-categories/index-categories
      (fn []
        [error-boundary
         [c.admin-index-categories/index-categories store items]]))

    (deftest index-categories-test
      (is (vector? (c.admin-index-categories/index-categories store items)))))

  (let [store (test-store)]
    (st/dispatch store [::e.users/do-fetch-index-success {:items items}])

    (defcard-rg c.admin-index-users/section
      (fn []
        [error-boundary
         [c.admin-index-users/section store]]))

    (deftest section-test
      (is (vector? (c.admin-index-users/section store))))))
