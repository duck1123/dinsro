(ns dinsro.ui.forms.admin.users
  (:require
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.mutations :as fm]
   [com.fulcrologic.rad.form :as form]
   [com.fulcrologic.rad.form-options :as fo]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-field :refer [ui-form-field]]
   [com.fulcrologic.semantic-ui.collections.form.ui-form-input :as ufi :refer [ui-form-input]]
   [dinsro.model.users :as m.users]
   [dinsro.options.users :as o.users]
   [dinsro.ui.debug :as u.debug]))

;; [[../../../../../test/dinsro/ui/forms/admin/user_test.cljs]]

(def debug-props? true)
(def override-form? false)

(form/defsc-form UserForm
  [this {name     o.users/name
         password o.users/password
         :as      props}]
  {fo/attributes   [m.users/name
                    m.users/role
                    m.users/password]
   fo/cancel-route ["users"]
   fo/id           m.users/id
   fo/route-prefix "forms/users"
   fo/title        "Admin User"}
  (dom/div {}
    (if override-form?
      (form/render-layout this props)
      (dom/div :.ui
        (dom/p {} "Admin User Form")
        (ui-form-field {}
          (ui-form-input
           {:value    (str name)
            :onChange (fn [evt _] (fm/set-string! this o.users/name :event evt))
            :label    "Name"}))
        (ui-form-field {}
          (ui-form-input
           {:value    (str password)
            :onChange (fn [evt _] (fm/set-string! this o.users/password :event evt))
            :label    "Password"}))))
    (when debug-props?
      (u.debug/log-props props))))
