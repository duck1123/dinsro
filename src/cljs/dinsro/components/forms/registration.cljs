(ns dinsro.components.forms.registration
  (:require
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec.events.forms.registration :as s.e.f.registration]
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(defn on-submit
  [form-data e]
  (.preventDefault e)
  (rf/dispatch [::e.authentication/submit-registration form-data]))

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.registration/form-data])
        error-message @(rf/subscribe [::s.e.f.registration/error-message])]
    [:div.box
     [:form {:on-submit (partial on-submit form-data)}
      [c/error-message-box error-message]
      [c.debug/debug-box form-data]
      [c/text-input "Name" ::s.e.f.registration/name]
      [c/email-input "Email" ::s.e.f.registration/email]
      [c/password-input "Password" ::s.e.f.registration/password]
      [c/password-input "Confirm Password" ::s.e.f.registration/confirm-password]
      [c/primary-button (tr [:submit]) [::e.authentication/submit-registration form-data]]]]))
