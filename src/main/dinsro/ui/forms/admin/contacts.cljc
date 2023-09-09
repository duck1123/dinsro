(ns dinsro.ui.forms.admin.contacts
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [dinsro.model.contacts :as m.contacts]
   [dinsro.options.contacts :as o.contacts]
   [dinsro.ui.pickers :as u.pickers]))

(def override-form true)

(form/defsc-form NewForm
  [this props]
  {fo/attributes    [m.contacts/name m.contacts/user]
   fo/cancel-route  ["admin"]
   fo/field-options {o.contacts/user u.pickers/admin-user-picker}
   fo/field-styles  {o.contacts/user :pick-one}
   fo/id            m.contacts/id
   fo/route-prefix  "new-contact"
   fo/title         "Contact"}
  (if override-form
    (form/render-layout this props)
    (dom/div {} (dom/p {} "Contact"))))
