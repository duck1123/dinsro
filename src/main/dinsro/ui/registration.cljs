(ns dinsro.ui.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.forms.registration :as u.f.registration]))

(defsc RegistrationPage
  [_this {::keys [allow-registration form]}]
  {:ident         (fn [_] [:component/id ::RegistrationPage])
   :initial-state {::allow-registration true
                   ::form               {}}
   :query         [::allow-registration
                   {::form (comp/get-query u.f.registration/RegistrationForm)}]
   :route-segment ["register"]}
  (if allow-registration
    (dom/div :.ui.container.center.aligned
      (dom/h4 :.ui.center.aligned.top.attached.header "Register")
      (dom/div :.ui.center.aligned.attached.segment
        (u.f.registration/ui-registration-form form)))
    (dom/div {}
      (dom/p {} "Registrations are not enabled"))))
