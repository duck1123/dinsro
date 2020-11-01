(ns dinsro.components.forms.login
  (:require
   [dinsro.components :as c]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.forms.login :as e.f.login]
   [dinsro.specs.events.forms.login :as s.e.f.login]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn on-submit
  [store return-to e]
  (timbre/infof "submit: %s" e)
  (let [form-data @(st/subscribe store [::e.f.login/form-data])]
    (st/dispatch store [::e.authentication/do-authenticate form-data return-to]))
  (.preventDefault e))

(defn form
  [store return-to]
  (let [form-data @(st/subscribe store [::e.f.login/form-data])]
    [:form.is-centered {:on-submit (partial on-submit store return-to)}
     [:div.field>div.control
      [c/email-input store (tr [:email]) ::s.e.f.login/email]]
     [:div.field>div.control
      [c/password-input store (tr [:password]) ::s.e.f.login/password]]
     [:div.field>div.control
      [c/primary-button
       store
       (tr [:login])
       [::e.authentication/do-authenticate form-data return-to]]]]))
