(ns dinsro.ui.admin-index-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.categories :as m.categories]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.create-category :as u.f.create-category]
   [dinsro.ui.links :as u.links]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as log]))

(def form-toggle-sm ::form-toggle)

(defsc AdminIndexCategoryLine
  [_this {::m.categories/keys [id user link]}]
  {:ident         ::m.categories/id
   :initial-state {::m.categories/id   nil
                   ::m.categories/link []
                   ::m.categories/user []}
   :query         [::m.categories/id
                   {::m.categories/link (comp/get-query u.links/CategoryLink)}
                   ::m.categories/name
                   {::m.categories/user (comp/get-query u.links/UserLink)}]}
  (dom/tr {}
    (dom/td (u.links/ui-category-link (first link)))
    (dom/td (u.links/ui-user-link (first user)))
    (dom/td (u.buttons/ui-delete-category-button {::m.categories/id id}))))

(def ui-admin-index-category-line (comp/factory AdminIndexCategoryLine {:keyfn ::m.categories/id}))

(defsc AdminIndexCategories
  [this {::keys [categories form toggle-button]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar AdminIndexCategories})
   :ident             (fn [_] [:component/id ::AdminIndexCategories])
   :initial-state     {::categories    []
                       ::form          {}
                       ::toggle-button {:form-button/id form-toggle-sm}}
   :query             [{::categories (comp/get-query AdminIndexCategoryLine)}
                       {::form (comp/get-query u.f.create-category/CreateCategoryForm)}
                       {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
                       [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2 :.title.is-2
       (tr [:categories "Categories"])
       (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.create-category/ui-create-category-form form))
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
           (map ui-admin-index-category-line categories)))))))

(def ui-section (comp/factory AdminIndexCategories))
