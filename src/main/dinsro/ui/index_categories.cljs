(ns dinsro.ui.index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc IndexCategoryLine
  [_this {::m.categories/keys [name user]}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id   ""
                   ::m.categories/name ""
                   ::m.categories/user {}}
   :query         [::m.categories/id
                   ::m.categories/name
                   {::m.categories/user (comp/get-query u.links/UserLink)}]}
  (dom/tr {}
    (dom/td name)
    (dom/td (u.links/ui-user-link user))))

(def ui-index-category-line (comp/factory IndexCategoryLine {:keyfn ::m.categories/id}))

(defsc IndexCategories
  [_this {::keys [categories]}]
  {:initial-state {::categories []}
   :query         [{::categories (comp/get-query IndexCategoryLine)}]}
  (if (seq categories)
    (dom/div {}
      (dom/table :.table.is-fullwidth
        (dom/thead {}
          (dom/tr {}
            (dom/th (tr [:name]))
            (dom/th (tr [:user]))))
        (dom/tbody {}
          (map ui-index-category-line categories))))
    (dom/p (tr [:no-categories]))))

(def ui-index-categories (comp/factory IndexCategories))
