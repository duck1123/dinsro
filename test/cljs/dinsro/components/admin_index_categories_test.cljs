(ns dinsro.components.admin-index-categories-test
  (:require
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.admin-index-categories :as c.admin-index-categories]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.spec :as ds]
   [dinsro.spec.categories :as s.categories]
   [dinsro.translations :refer [tr]]))

(cards/header "Admin Index Categories Components" [])

(let [items (ds/gen-key (s/coll-of ::s.categories/item :count 3))]
  (defcard items items)

  (defcard-rg c.admin-index-categories/category-line
    (fn []
      [error-boundary
       [c.admin-index-categories/category-line (first items)]]))

  (defcard-rg c.admin-index-categories/index-categories
    (fn []
      [error-boundary
       [c.admin-index-categories/index-categories items]]))

  (defcard-rg c.admin-index-categories/section
    (fn []
      [error-boundary
       [c.admin-index-categories/section]])))
