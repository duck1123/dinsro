(ns dinsro.ui.user-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-category :as u.f.add-user-category]
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
  [_this {::keys [categories]}]
  {:initial-state {::categories []}
   :query [{::categories (comp/get-query IndexCategoryLine)}]}
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
  [_this {::keys [categories form toggle-button]}]
  {:initial-state {::categories {}
                   ::form {}
                   ::toggle-button {}}
   :query [{::categories (comp/get-query IndexUserCategories)}
           {::form (comp/get-query u.f.add-user-category/AddUserCategoryForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}]}
  (bulma/box
   (dom/h2 (tr [:categories]) (u.buttons/ui-show-form-button toggle-button))
   (u.f.add-user-category/ui-form form)
   (ui-index-user-categories categories)))

(def ui-user-categories
  (comp/factory UserCategories))
