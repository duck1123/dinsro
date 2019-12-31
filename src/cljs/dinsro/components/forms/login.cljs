(ns dinsro.components.forms.login
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn form
  [return-to]
  (let [form-data @(rf/subscribe [::form-data])]
    [:form.is-centered
     [c.debug/debug-box form-data]
     [c/email-input (tr [:email]) ::email ::set-email]
     [c/password-input (tr [:password]) ::password ::set-password]
     [c/primary-button (tr [:login]) [::e.authentication/do-authenticate form-data return-to]]]))
