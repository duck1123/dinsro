(ns dinsro.components.forms.registration-form
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn-spec registration-form vector?
  []
  (let [form-data @(rf/subscribe [::form-data])]
    [:div.box
     [:form
      [c.debug/debug-box form-data]
      [c/text-input     "Name"             ::name             ::set-name]
      [c/email-input    "Email"            ::email            ::set-email]
      [c/password-input "Password"         ::password         ::set-password]
      [c/password-input "Confirm Password" ::confirm-password ::set-confirm-password]
      [c/primary-button (tr [:submit]) [::submit-clicked form-data]]]]))
