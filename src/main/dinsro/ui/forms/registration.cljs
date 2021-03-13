(ns dinsro.ui.forms.registration
  (:require
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.specs.events.forms.registration :as s.e.f.registration]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defn on-submit
  [store form-data e]
  (.preventDefault e)
  (st/dispatch store [::e.authentication/submit-registration form-data]))

(defn form
  [store]
  (let [form-data @(st/subscribe store [::e.f.registration/form-data])
        error-message @(st/subscribe store [::s.e.f.registration/error-message])]
    [:div.box
     [:form {:on-submit (partial on-submit store form-data)}
      [u.debug/error-message-box error-message]
      [u.inputs/text-input store "Name" ::s.e.f.registration/name]
      [u.inputs/email-input store "Email" ::s.e.f.registration/email]
      [u.inputs/password-input store "Password" ::s.e.f.registration/password]
      [u.inputs/password-input store "Confirm Password" ::s.e.f.registration/confirm-password]
      [u.inputs/primary-button store (tr [:submit]) [::e.authentication/submit-registration form-data]]]]))
