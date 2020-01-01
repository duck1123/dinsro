(ns dinsro.components.forms.create-rate-source
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.create-rate-source :as e.f.create-rate-source]
            [dinsro.events.rate-sources :as e.rate-sources]
            [dinsro.spec.events.forms.create-rate-source :as s.e.f.create-rate-source]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::e.f.create-rate-source/init-form]})

(defn form-shown
  []
  (let [form-data @(rf/subscribe [::e.f.create-rate-source/form-data])]
    [:<>
     [c/close-button ::e.f.create-rate-source/set-shown?]
     [c.debug/debug-box form-data]
     [c/text-input (tr [:name]) ::s.e.f.create-rate-source/name]
     [c/text-input (tr [:url]) ::s.e.f.create-rate-source/url]
     [c/currency-selector (tr [:currency]) ::s.e.f.create-rate-source/currency-id]
     [:div.field>div.control
      [c/primary-button (tr [:submit]) [::e.rate-sources/do-submit form-data]]]]))

(defn form
  []
  (when @(rf/subscribe [::e.f.create-rate-source/shown?])
    [form-shown]))
