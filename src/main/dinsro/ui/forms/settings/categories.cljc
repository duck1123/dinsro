(ns dinsro.ui.forms.settings.categories
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.categories :as m.categories]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.options.categories :as o.categories]
   [dinsro.ui.buttons :as u.buttons]))

(def override-form true)

(def create-action
  (u.buttons/form-action-button
   "Create" mu.categories/create!
   #{o.categories/name}))

(form/defsc-form NewForm
  [_this _props]
  {fo/action-buttons [::create]
   fo/attributes     [m.categories/name]
   fo/cancel-route   ["categories"]
   fo/field-styles   {o.categories/user :link}
   fo/controls       (merge form/standard-controls {::create create-action})
   fo/id             m.categories/id
   fo/route-prefix   "new-category"
   fo/title          "New Category"})

(form/defsc-form CategoryForm
  [this props]
  {fo/attributes   [m.categories/name
                    m.categories/user]
   fo/cancel-route ["categories"]
   fo/field-styles {o.categories/user :link}
   fo/id           m.categories/id
   fo/route-prefix "category"
   fo/title        "Edit Category"}
  (if override-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Category"))))
