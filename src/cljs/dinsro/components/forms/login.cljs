(ns dinsro.components.forms.login
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.spec.events.forms.login :as s.e.f.login]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn form
  [return-to]
  (let [form-data @(rf/subscribe [::s.e.f.login/form-data])]
    [:form.is-centered
     [c.debug/debug-box form-data]
     [c/email-input (tr [:email]) ::s.e.f.login/email ::s.e.f.login/set-email]
     [c/password-input (tr [:password]) ::s.e.f.login/password ::s.e.f.login/set-password]
     [c/primary-button (tr [:login]) [::e.authentication/do-authenticate form-data return-to]]]))
