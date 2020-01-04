(ns dinsro.components.admin-index-categories-test
  (:require [clojure.spec.alpha :as s]
            [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components.admin-index-categories :as c.admin-index-categories]
            [dinsro.spec :as ds]
            [dinsro.spec.categories :as s.categories]
            [dinsro.translations :refer [tr]]))

(let [items (ds/gen-key (s/coll-of ::s.categories/item :count 3))]
  (defcard items items)

  (defcard-rg c.admin-index-categories/category-line
    [c.admin-index-categories/category-line (first items)])

  (defcard-rg c.admin-index-categories/index-categories
    [c.admin-index-categories/index-categories items])

  (defcard-rg c.admin-index-categories/section
    [c.admin-index-categories/section]))
