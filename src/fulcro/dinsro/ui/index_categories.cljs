(ns dinsro.ui.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.sample :as sample]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexCategoryLine
  [_this {::m.categories/keys [name user-id]}]
  (dom/tr
   (dom/td name)
   (dom/td user-id)))

(def ui-index-category-line (comp/factory IndexCategoryLine {:keyfn ::m.categories/id}))

(defsc IndexCategories
  [_this {categories :categories/list}]
  {:query [:categories/list]
   :initial-state (fn [_] {:categories/list (vals sample/category-map)})}
  (if (seq categories)
    (dom/div
     (dom/p "Index Categories")
     (dom/table
      :.table
      (dom/thead
       (dom/tr
        (dom/th (tr [:name]))
        (dom/th (tr [:user]))))
      (dom/tbody
       (map ui-index-category-line categories))))
    (dom/p (tr [:no-categories]))))

(def ui-index-categories (comp/factory IndexCategories))
