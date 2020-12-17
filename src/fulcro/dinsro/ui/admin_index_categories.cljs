(ns dinsro.ui.admin-index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.forms.create-category :as u.f.create-category]
   [dinsro.translations :refer [tr]]))

(defsc AdminIndexCategoryLine
  [_this {::m.categories/keys [name user-id]}]
  {:query [::m.categories/id
           ::m.categories/name
           ::m.categories/user-id]
   :ident ::m.categories/id
   :initial-state {::m.categories/id 0
                   ::m.categories/name ""
                   ::m.categories/user-id 0}}
  (dom/tr
   (dom/td name)
   (dom/td user-id)
   (dom/td (dom/button :.button.is-danger "Delete"))))

(def ui-admin-index-category-line (comp/factory AdminIndexCategoryLine))

(defsc AdminIndexCategories
  [_this {:keys [categories]}]
  {:query [:categories]
   :initial-state {:categories []}}
  (bulma/box
   (dom/h1
    (tr [:categories "Categories"])
    (dom/button "+"))
   (u.f.create-category/ui-create-category-form)
   (dom/hr)
   (if (empty? categories)
     (dom/p (tr [:no-categories]))
     (dom/table
      :.table
      (dom/thead
       (dom/tr
        (dom/th (tr [:name]))
        (dom/th (tr [:user]))
        (dom/th (tr [:actions]))))
      (dom/tbody
       (map ui-admin-index-category-line categories))))))

(def ui-section (comp/factory AdminIndexCategories))
