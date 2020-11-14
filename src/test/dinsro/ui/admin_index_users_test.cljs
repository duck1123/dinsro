(ns dinsro.ui.admin-index-users-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.users :as e.users]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [dinsro.ui.admin-index-users :as u.admin-index-users]
   [dinsro.ui.boundary]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.debug/init-handlers!
                e.users/init-handlers!)]
    store))

(let [items (ds/gen-key (s/coll-of ::m.users/item :count 3))
      item (first items)]
  (let [store (test-store)]
    (st/dispatch store [::e.users/do-fetch-index-success {:items items}])

    (defcard-rg category-line
      [:table.table>tbody
       [u.admin-index-categories/category-line store item]])

    (deftest category-line-test
      (is (vector? [u.admin-index-categories/category-line store item]))))

  (let [store (test-store)]
    (st/dispatch store [::e.users/do-fetch-index-success {:items items}])

    (defcard-rg index-categories
      [u.admin-index-categories/index-categories store items])

    (deftest index-categories-test
      (is (vector? (u.admin-index-categories/index-categories store items)))))

  (let [store (test-store)]
    (st/dispatch store [::e.users/do-fetch-index-success {:items items}])

    (defcard-rg section
      [u.admin-index-users/section store])

    (deftest section-test
      (is (vector? (u.admin-index-users/section store))))))
