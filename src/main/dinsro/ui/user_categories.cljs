(ns dinsro.ui.user-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(defsc IndexCategoryLine
  [_this {::m.categories/keys [id link]}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id   nil
                   ::m.categories/name ""
                   ::m.categories/link {}}
   :query         [{::button-data (comp/get-query u.buttons/DeleteCategoryButton)}
                   ::m.categories/id
                   {::m.categories/link (comp/get-query u.links/CategoryLink)}
                   ::m.categories/name]}
  (if id
    (dom/tr {}
      (dom/td (u.links/ui-category-link link))
      (dom/td (u.buttons/ui-delete-category-button {::m.categories/id id})))
    (dom/p {} "No id")))

(def ui-index-category-line (comp/factory IndexCategoryLine {:keyfn ::m.categories/id}))

(defsc UserCategories
  [_this {::m.users/keys [id categories]}]
  {:ident             ::m.users/id
   :initial-state     {::m.users/categories {}}
   :query             [::m.users/id
                       {::m.users/categories (comp/get-query IndexCategoryLine)}]}
  (if id
    (bulma/box
     (dom/h2 {}
       (tr [:categories]))
     (dom/hr)
     (if (seq categories)
       (dom/table :.ui.table
         (dom/thead {}
           (dom/tr {}
             (dom/th (tr [:name]))
             (dom/th (tr [:actions]))))
         (dom/tbody {}
           (map ui-index-category-line categories)))
       (dom/div (tr [:no-categories]))))
    (dom/p {} "User Categories Not loaded")))

(def ui-user-categories (comp/factory UserCategories))
