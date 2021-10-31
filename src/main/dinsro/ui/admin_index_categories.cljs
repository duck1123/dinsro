(ns dinsro.ui.admin-index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.links :as u.links]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(defsc AdminIndexCategoryLine
  [_this {::m.categories/keys [id user link]}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id   nil
                   ::m.categories/link []
                   ::m.categories/user []}
   :query         [::m.categories/id
                   {::m.categories/link (comp/get-query u.links/CategoryLink)}
                   {::m.categories/user (comp/get-query u.links/UserLink)}]}
  (dom/tr {}
    (dom/td (u.links/ui-category-link link))
    (dom/td (u.links/ui-user-link user))
    (dom/td (u.buttons/ui-delete-category-button {::m.categories/id id}))))

(def ui-admin-index-category-line (comp/factory AdminIndexCategoryLine {:keyfn ::m.categories/id}))

(defsc AdminIndexCategories
  [_this {::keys [categories]}]
  {:ident             (fn [_] [:component/id ::AdminIndexCategories])
   :initial-state     {::categories    []}
   :query             [{::categories (comp/get-query AdminIndexCategoryLine)}]}
  (bulma/box
   (dom/h2 :.title.is-2
     (tr [:categories "Categories"]))
   (dom/hr)
   (if (empty? categories)
     (dom/p (tr [:no-categories]))
     (dom/table :.table.ui
       (dom/thead {}
         (dom/tr {}
           (dom/th (tr [:name]))
           (dom/th (tr [:user]))
           (dom/th (tr [:actions]))))
       (dom/tbody {}
         (map ui-admin-index-category-line categories))))))

(def ui-section (comp/factory AdminIndexCategories))
