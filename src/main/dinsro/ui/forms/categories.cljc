(ns dinsro.ui.forms.categories
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.categories :as m.categories]
   [dinsro.options.categories :as o.categories]
   [dinsro.ui.debug :as u.debug]))

(form/defsc-form NewForm
  [_this _props]
  {fo/attributes   [m.categories/name]
   fo/cancel-route ["categories"]
   fo/field-styles {o.categories/user :link}
   fo/id           m.categories/id
   fo/route-prefix "new-category"
   fo/title        "New Category"})

(def override-form true)

(form/defsc-form CategoryForm
  [this props]
  {fo/attributes   [m.categories/name
                    m.categories/user]
   fo/cancel-route ["categories"]
   fo/field-styles {o.categories/user :link}
   fo/id           m.categories/id
   fo/route-prefix "category"
   fo/title        "Edit Category"}
  (dom/div {}
    (if override-form
      (form/render-layout this props)
      (dom/div {}
        (dom/p {} "Category")))
    (u.debug/ui-props-logger props)))
