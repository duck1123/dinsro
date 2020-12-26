(ns dinsro.views.registration
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.forms.registration :as u.f.registration]
   [taoensso.timbre :as timbre]))

(defsc RegistrationPage
  [_this {:keys [registration-form-data]}]
  {:query [{:registration-form-data (comp/get-query u.f.registration/RegistrationForm)}]
   :route-segment ["registration"]}
  (let [allow-registration true]
    (bulma/section
     (bulma/container
      (bulma/content
       (if allow-registration
         (dom/div
          (dom/h1 "Registration Page")
          (u.f.registration/ui-registration-form registration-form-data))
         (dom/div
          (dom/p "Registrations are not enabled"))))))))

(def ui-page (comp/factory RegistrationPage))
