(ns dinsro.ui.admin-index-categories-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.users :as e.users]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-categories :as u.admin-index-categories]
   [dinsro.ui.boundary]
   [taoensso.timbre :as timbre]))

(def users (ds/gen-key (s/coll-of ::m.users/item :count 3)))

(def categories (map
                 (fn [category]
                   (let [user-ids (map :db/id users)
                         user-id (rand-nth user-ids)]
                     (assoc-in category [::m.categories/user :db/id] user-id)))
                 (ds/gen-key (s/coll-of ::m.categories/item :count 3))))
(def category (first categories))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.debug/init-handlers!
                e.categories/init-handlers!
                e.users/init-handlers!
                e.f.create-category/init-handlers!)]
    store))

(let [store (test-store)]
  (st/dispatch store [::e.categories/do-fetch-index-success {:items categories}])
  (st/dispatch store [::e.users/do-fetch-index-success {:users users}])
  ;; (st/dispatch store [::e.f.create-categories/set-shown? true])

  (defcard-rg category-line
    [:table.table>tbody
     [u.admin-index-categories/category-line store category]])

  (deftest category-line-test
    (is (vector? (u.admin-index-categories/category-line store category))))

  (defcard-rg index-categories
    [u.admin-index-categories/index-categories store categories])

  (deftest index-categories-test
    (is (vector? (u.admin-index-categories/index-categories store categories))))

  (defcard-rg section
    [u.admin-index-categories/section store])

  (deftest section-test
    (is (vector? (u.admin-index-categories/section store)))))
