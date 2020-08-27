(ns dinsro.components.forms.create-rate-source
  (:require
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.forms.create-rate-source :as e.f.create-rate-source]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.spec.events.forms.create-rate-source :as s.e.f.create-rate-source]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(comment
  (kf/reg-controller
   ::form-controller
   {:params (constantly true)
    :start [::e.f.create-rate-source/init-form]}))

(defn form-shown
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-rate-source/form-data])]
    [:<>
     [c/close-button store ::e.f.create-rate-source/set-shown?]
     [c.debug/debug-box store form-data]
     [c/text-input store (tr [:name]) ::s.e.f.create-rate-source/name]
     [c/text-input store (tr [:url]) ::s.e.f.create-rate-source/url]
     [c/currency-selector store (tr [:currency]) ::s.e.f.create-rate-source/currency-id]
     [:div.field>div.control
      [c/primary-button store (tr [:submit]) [::e.rate-sources/do-submit form-data]]]]))

(defn form
  [store]
  (when @(st/subscribe store [::e.f.create-rate-source/shown?])
    [form-shown store]))
