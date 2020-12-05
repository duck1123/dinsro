(ns dinsro.ui.forms.create-rate-source
  (:require
   [dinsro.events.forms.create-rate-source :as e.f.create-rate-source]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.specs.events.forms.create-rate-source :as s.e.f.create-rate-source]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::e.f.create-rate-source/init-form]})

(defn form-shown
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-rate-source/form-data])]
    [:div
     [u.buttons/close-button store ::e.f.create-rate-source/set-shown?]
     [u.inputs/text-input store (tr [:name]) ::s.e.f.create-rate-source/name]
     [u.inputs/text-input store (tr [:url]) ::s.e.f.create-rate-source/url]
     [u.inputs/currency-selector store (tr [:currency]) ::s.e.f.create-rate-source/currency-id]
     [:div.field>div.control
      [u.inputs/primary-button store (tr [:submit]) [::e.rate-sources/do-submit form-data]]]]))

(defn form
  [store]
  (when @(st/subscribe store [::e.f.create-rate-source/shown?])
    [form-shown store]))
