(ns dinsro.ui.user-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.users :as m.users]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-category :as u.f.add-user-category]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

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

(defsc IndexUserCategories
  [_this {::keys [categories]}]
  {:initial-state {::categories []}
   :query         [{::categories (comp/get-query IndexCategoryLine)}]}
  (if (seq categories)
    (dom/table :.table
      (dom/thead {}
        (dom/tr {}
          (dom/th (tr [:name]))
          (dom/th (tr [:actions]))))
      (dom/tbody {}
        (map ui-index-category-line categories)))
    (dom/div (tr [:no-categories]))))

(def ui-index-user-categories (comp/factory IndexUserCategories))

(defsc UserCategories
  [this {::keys         [form toggle-button]
         ::m.users/keys [id categories]}]
  {:componentDidMount (fn [this]
                        (uism/begin! this machines/hideable
                                     form-toggle-sm
                                     {:actor/navbar (uism/with-actor-class [::m.users/id :none]
                                                      UserCategories)}))
   :ident             ::m.users/id
   :initial-state     {::m.users/categories {}
                       ::form               {}
                       ::toggle-button      {:form-button/id form-toggle-sm}}
   :pre-merge         (fn [{:keys [current-normalized data-tree]}]
                        (let [defaults    {::form          (comp/get-initial-state u.f.add-user-category/AddUserCategoryForm)
                                           ::toggle-button {:form-button/id form-toggle-sm}}
                              merged-data (merge current-normalized data-tree defaults)]
                          merged-data))
   :query             [::m.users/id
                       {::m.users/categories (comp/get-query IndexCategoryLine)}
                       {::form (comp/get-query u.f.add-user-category/AddUserCategoryForm)}
                       {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                       [::uism/asm-id form-toggle-sm]]}
  (if id
    (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
      (bulma/box
       (dom/h2 {}
         (tr [:categories])
         (when toggle-button (u.buttons/ui-show-form-button toggle-button)))
       (when shown?
         (when form (u.f.add-user-category/ui-form form)))
       (dom/hr)
       (if (seq categories)
         (dom/table :.ui.table
           (dom/thead {}
             (dom/tr {}
               (dom/th (tr [:name]))
               (dom/th (tr [:actions]))))
           (dom/tbody {}
             (map ui-index-category-line categories)))
         (dom/div (tr [:no-categories])))))
    (dom/p {} "User Categories Not loaded")))

(def ui-user-categories (comp/factory UserCategories))
