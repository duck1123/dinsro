(ns dinsro.components.forms.registration
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.registration :as e.f.registration]
            [dinsro.spec.events.forms.registration :as s.e.f.registration]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.registration/form-data])]
    [:div.box
     [:form
      [c.debug/debug-box form-data]
      [c/text-input     "Name"
       ::s.e.f.registration/name             ::s.e.f.registration/set-name]
      [c/email-input    "Email"
       ::s.e.f.registration/email            ::s.e.f.registration/set-email]
      [c/password-input "Password"
       ::s.e.f.registration/password         ::s.e.f.registration/set-password]
      [c/password-input "Confirm Password"
       ::s.e.f.registration/confirm-password ::s.e.f.registration/set-confirm-password]
      [c/primary-button (tr [:submit]) [::submit-clicked form-data]]]]))
