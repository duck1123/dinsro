(ns dinsro.ui.forms.create-rate
  (:require
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.events.rates :as e.rates]
   [dinsro.specs.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.datepicker :as u.datepicker]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::e.f.create-rate/init-form]})

(defn form-shown
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-rate/form-data])]
    [:div
     [u/close-button store ::e.f.create-rate/set-shown?]
     [:div.field>div.control
      [u/number-input store (tr [:rate]) ::s.e.f.create-rate/rate]]
     [:div.field>div.control
      [:label.label (tr [:date])]
      [u.datepicker/datepicker {:on-select #(st/dispatch store [::s.e.f.create-rate/set-date %])}]]
     [:div.field>div.control
      [u/currency-selector store (tr [:currency]) ::s.e.f.create-rate/currency-id]]
     [:div.field>div.control
      [u/primary-button store (tr [:submit]) [::e.rates/do-submit form-data]]]]))

(defn form
  [store]
  (when @(st/subscribe store [::e.f.create-rate/shown?])
    [form-shown store]))
