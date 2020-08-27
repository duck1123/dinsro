(ns dinsro.components.forms.registration
  (:require
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.forms.registration :as e.f.registration]
   [dinsro.spec.events.forms.registration :as s.e.f.registration]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
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
      [c/error-message-box error-message]
      [c.debug/debug-box store form-data]
      [c/text-input store "Name" ::s.e.f.registration/name]
      [c/email-input store "Email" ::s.e.f.registration/email]
      [c/password-input store "Password" ::s.e.f.registration/password]
      [c/password-input store "Confirm Password" ::s.e.f.registration/confirm-password]
      [c/primary-button store (tr [:submit]) [::e.authentication/submit-registration form-data]]]]))
