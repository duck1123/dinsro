(ns dinsro.components.forms.login
  (:require
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.forms.login :as e.f.login]
   [dinsro.spec.events.forms.login :as s.e.f.login]
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(defn form
  [return-to]
  (let [form-data @(rf/subscribe [::e.f.login/form-data])]
    [:form.is-centered
     {:on-submit (fn [e]
                   (rf/dispatch [::e.authentication/do-authenticate form-data return-to])
                   (.preventDefault e))}
     [c.debug/debug-box form-data]
     [:div.field>div.control
      [c/email-input (tr [:email]) ::s.e.f.login/email]]
     [:div.field>div.control
      [c/password-input (tr [:password]) ::s.e.f.login/password]]
     [:div.field>div.control
      [:button
       {:type "submit"}
       "Submit me!"]
      #_[c/primary-button (tr [:login]) [::e.authentication/do-authenticate form-data return-to]]]]))
