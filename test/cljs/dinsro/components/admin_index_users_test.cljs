(ns dinsro.components.admin-index-users-test
  (:require
   [cljs.test :refer [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
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

(defcard-rg title
  [:div
   [:h1.title "Admin Index Users Components"]
   [:ul.box
    [:li
     [:a {:href "devcards.html#!/dinsro.components_test"}
      "Components"]]]])

(let [items (ds/gen-key (s/coll-of ::s.users/item :count 3))
      store (doto (mock-store)
                e.debug/init-handlers!
                e.users/init-handlers!)]
  (st/dispatch store [::e.users/do-fetch-index-success {:items items}])

  (defcard items items)

  (defcard-rg c.admin-index-categories/category-line
    (fn []
      [error-boundary
       [c.admin-index-categories/category-line store (first items)]]))

  (defcard-rg c.admin-index-categories/index-categories
    (fn []
      [error-boundary
       [c.admin-index-categories/index-categories store items]]))

  (deftest section-test
    (is (vector? (c.admin-index-users/section store ))))

  (defcard-rg c.admin-index-users/section
    (fn []
      [error-boundary
       [c.admin-index-users/section store]])))
