(ns dinsro.ui.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defsc IndexCategoryLine
  [_this {::m.categories/keys [name user-id]}]
  {:query [::m.categories/name
           ::m.categories/user-id]
   :initial-state
   (fn [{::m.categories/keys [id]}]
     {::m.categories/id id
      ::m.categories/name ""
      ::m.categories/user-id 0})}
  (dom/tr
   (dom/td name)
   (dom/td user-id)))

(def ui-index-category-line (comp/factory IndexCategoryLine {:keyfn ::m.categories/id}))

(defsc IndexCategories
  [_this {::keys [categories]}]
  {:query [{::categories (comp/get-query IndexCategoryLine)}]
   :initial-state
   (fn [_]
     (let [ids [1]]
       {::categories (map #(comp/get-initial-state IndexCategoryLine {::m.categories/id %}) ids)}))}
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
