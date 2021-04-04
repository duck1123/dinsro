(ns dinsro.ui.user-categories
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.ui-state-machines :as uism]
   [dinsro.machines :as machines]
   [dinsro.model.categories :as m.categories]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-category :as u.f.add-user-category]
   [dinsro.ui.links :as u.links]
   [taoensso.timbre :as timbre]))

(def form-toggle-sm ::form-toggle)

(defsc IndexCategoryLine
  [_this {::m.categories/keys [id link]}]
  {:ident ::m.categories/id
   :initial-state {::m.categories/id   0
                   ::m.categories/name ""
                   ::m.categories/link {}}
   :query [{::button-data (comp/get-query u.buttons/DeleteCategoryButton)}
           ::m.categories/id
           {::m.categories/link (comp/get-query u.links/CategoryLink)}
           ::m.categories/name]}
  (dom/tr
   (dom/td (u.links/ui-category-link (first link)))
   (dom/td (u.buttons/ui-delete-category-button {::m.categories/id id}))))

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
       (dom/th (tr [:name]))
       (dom/th (tr [:actions]))))
     (dom/tbody
      (map ui-index-category-line categories)))
    (dom/div (tr [:no-categories]))))

(def ui-index-user-categories (comp/factory IndexUserCategories))

(defsc UserCategories
  [this {::keys [categories form toggle-button]}]
  {:componentDidMount #(uism/begin! % machines/hideable form-toggle-sm {:actor/navbar UserCategories})
   :ident (fn [_] [:component/id ::UserCategories])
   :initial-state {::categories    {}
                   ::form          {}
                   ::toggle-button {:form-button/id form-toggle-sm}}
   :query [{::categories    (comp/get-query IndexUserCategories)}
           {::form          (comp/get-query u.f.add-user-category/AddUserCategoryForm)}
           {::toggle-button (comp/get-query u.buttons/ShowFormButton)}
           [::uism/asm-id form-toggle-sm]]}
  (let [shown? (= (uism/get-active-state this form-toggle-sm) :state/shown)]
    (bulma/box
     (dom/h2 (tr [:categories]) (u.buttons/ui-show-form-button toggle-button))
     (when shown?
       (u.f.add-user-category/ui-form form))
     (dom/hr)
     (ui-index-user-categories categories))))

(def ui-user-categories (comp/factory UserCategories))
