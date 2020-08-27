(ns dinsro.components.forms.create-currency
  (:require
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.create-currency :as e.f.create-currency]
   [dinsro.spec.events.forms.create-currency :as s.e.f.create-currency]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(comment
  (kf/reg-controller
   ::form-controller
   {:params (constantly true)
    :start [::e.f.create-currency/set-defaults]}))

(defn form
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-currency/form-data])]
    (when @(st/subscribe store [::e.f.create-currency/shown?])
      [:<>
       [c/close-button store ::e.f.create-currency/set-shown?]
       [c.debug/debug-box store form-data]
       [:form
        [c/text-input store (tr [:name]) ::s.e.f.create-currency/name]
        [c/primary-button store (tr [:submit]) [::e.currencies/do-submit form-data]]]])))
