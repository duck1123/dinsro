(ns dinsro.ui.user-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-categories :as u.f.add-user-categories]
   [taoensso.timbre :as timbre]))

(defsc IndexCategoryLine
  [_this {::m.categories/keys [id name]}]
  {:ident ::m.categories/id
   :query [::m.categories/id ::m.categories/name]}
  (dom/tr
   (dom/td id)
   (dom/td name)))

(def ui-index-category-line (comp/factory IndexCategoryLine {:keyfn ::m.categories/id}))

(defsc IndexUserCategories
  [_this {:keys [categories]}]
  {:query [{:categories (comp/get-query IndexCategoryLine)}]
   :initial-state {:categories []}}
  (if (seq categories)
    (dom/table
     :.table
     (dom/thead
      (dom/tr
       (dom/th "Id")
       (dom/th "name")))
     (dom/tbody
      (map ui-index-category-line categories)))
    (dom/div (tr [:no-categories]))))

(def ui-index-user-categories (comp/factory IndexUserCategories))

(defsc UserCategories
  [_this {:keys [form-data button-data index-data]}]
  {:query [{:form-data (comp/get-query u.f.add-user-categories/AddUserCategoriesForm)}
           {:button-data (comp/get-query u.buttons/ShowFormButton)}
           {:index-data (comp/get-query IndexUserCategories)}]
   :initial-state {:form-data {}
                   :button-data {}
                   :index-data {}}}
  (dom/div
   :.box
   (dom/h2 (tr [:categories]) (u.buttons/ui-show-form-button button-data))
   (u.f.add-user-categories/ui-form form-data)
   (ui-index-user-categories index-data)))

(def ui-user-categories
  (comp/factory UserCategories))
