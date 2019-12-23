(ns dinsro.components.forms.create-transaction
  (:require [dinsro.components :as c]
            [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.create-transaction/form-data])]
    (when @(rf/subscribe [::s.e.f.create-transaction/shown?])
      [:div
       [c/close-button ::s.e.f.create-transaction/set-shown?]
       [:div.field>div.control
        [c/number-input (tr [:value])
         ::s.e.f.create-transaction/value ::s.e.f.create-transaction/set-value]]
       [:div.field>div.control
        [c/currency-selector (tr [:currency])
         ::s.e.f.create-transaction/currency-id ::s.e.f.create-transaction/set-currency-id]]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::submit-clicked]]]])))
