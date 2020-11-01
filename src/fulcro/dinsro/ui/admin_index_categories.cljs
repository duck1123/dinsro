(ns dinsro.ui.admin-index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]))

(defsc AdminIndexCategoryLine
  [_this {:category/keys [name user-id]}]
  (dom/tr
   (dom/td name)
   (dom/td user-id)
   (dom/td (dom/button "Delete"))))

(def ui-admin-index-category-line (comp/factory AdminIndexCategoryLine))

(defsc AdminIndexCategories
  [_this {:keys [categories]}]
  {:query [:categories]
   :initial-state {:categories []}}
  (dom/div
   :.box
   (dom/h1
    (tr [:categories "Categories"])
    (dom/button "+"))
   (dom/div "Create category form")
   (dom/hr)
   (if (empty? categories)
     (dom/p (tr [:no-categories]))
     (dom/table
      (dom/thead
       (dom/tr
        (dom/th (tr [:name]))
        (dom/th (tr [:user]))
        (dom/th (tr [:actions]))))
      (dom/tbody
       (map ui-admin-index-category-line categories))))))
