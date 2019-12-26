(ns dinsro.components.forms.create-currency
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.forms.create-currency :as e.f.create-currency]
            [dinsro.spec.events.forms.create-currency :as s.e.f.create-currency]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::e.f.create-currency/set-defaults]})

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.create-currency/form-data])]
    (when @(rf/subscribe [::e.f.create-currency/shown?])
      [:<>
       [c/close-button ::e.f.create-currency/set-shown?]
       [c.debug/debug-box form-data]
       [:form
        [c/text-input (tr [:name]) ::s.e.f.create-currency/name]
        [c/primary-button (tr [:submit]) [::e.currencies/do-submit form-data]]]])))
