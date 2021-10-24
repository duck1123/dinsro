(ns dinsro.ui.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.user-categories :as u.user-categories]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc IndexCategoryLine
  [_this {::m.categories/keys [link user]}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id   nil
                   ::m.categories/link {}
                   ::m.categories/user {}}
   :query         [::m.categories/id
                   {::m.categories/link (comp/get-query u.links/CategoryLink)}
                   {::m.categories/user (comp/get-query u.links/UserLink)}]}
  (dom/tr {}
    (dom/td (u.links/ui-category-link link))
    (dom/td (u.links/ui-user-link user))))

(def ui-index-category-line (comp/factory IndexCategoryLine {:keyfn ::m.categories/id}))

(defsc IndexCategories
  [_this {::keys [categories]}]
  {:initial-state {::categories []}
   :query         [{::categories (comp/get-query IndexCategoryLine)}]}
  (if (seq categories)
    (dom/div {}
      (dom/table :.ui.table
        (dom/thead {}
          (dom/tr {}
            (dom/th (tr [:name]))
            (dom/th (tr [:user]))))
        (dom/tbody {}
          (map ui-index-category-line categories))))
    (dom/p (tr [:no-categories]))))

(def ui-index-categories (comp/factory IndexCategories))

(defsc IndexCategoriesPage
  [_this {::keys [categories]}]
  {:componentDidMount
   (fn [this]
     (df/load! this :session/current-user-ref
               u.user-categories/UserCategories
               {:target [:page/id
                         ::page
                         ::categories]}))
   :ident         (fn [] [:page/id ::page])
   :initial-state {::categories {}}
   :query         [:page/id
                   {::categories (comp/get-query u.user-categories/UserCategories)}]
   :route-segment ["categories"]}
  (bulma/page
   {:className "index-categories-page"}
   (when (::m.users/id categories)
     (u.user-categories/ui-user-categories categories))))

(def ui-page (comp/factory IndexCategoriesPage))
