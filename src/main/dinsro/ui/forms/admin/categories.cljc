(ns dinsro.ui.forms.admin.categories
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.categories :as m.categories]
   [dinsro.options.categories :as o.categories]
   [dinsro.ui.pickers :as u.pickers]))

(def override-admin-form true)

(form/defsc-form NewForm
  [this props]
  {fo/attributes    [m.categories/name m.categories/user]
   fo/cancel-route  ["admin"]
   fo/field-options {o.categories/user u.pickers/admin-user-picker}
   fo/field-styles  {o.categories/user :pick-one}
   fo/id            m.categories/id
   fo/route-prefix  "new-category"
   fo/title         "Category"}
  (if override-admin-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Category"))))
