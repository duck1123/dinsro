(ns dinsro.views.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.forms.registration :as u.f.registration]
   [taoensso.timbre :as log]))

(defsc RegistrationPage
  [_this {::keys [allow-registration form]}]
  {:ident         (fn [_] [:page/id ::page])
   :initial-state {::allow-registration true
                   ::form               {}}
   :query         [::allow-registration
                   {::form (comp/get-query u.f.registration/RegistrationForm)}]
   :route-segment ["register"]}
  (bulma/page
   (if allow-registration
     (dom/div
       (dom/h1 "Registration Page")
       (u.f.registration/ui-registration-form form))
     (dom/div
       (dom/p "Registrations are not enabled")))))

(def ui-page (comp/factory RegistrationPage))
