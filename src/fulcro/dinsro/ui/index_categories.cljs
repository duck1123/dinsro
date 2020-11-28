(ns dinsro.ui.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]))

(defsc IndexCategoryLine
  [_this {:user/keys [name]
          user-id :user/id}]
  (dom/tr
   (dom/td name)
   (dom/td user-id)))

(def ui-index-category-line (comp/factory IndexCategoryLine))

(defsc IndexCategories
  [_this {categories :categories/list}]
  {:query [:categories/list]
   :initial-state {:categories/list []}}
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
