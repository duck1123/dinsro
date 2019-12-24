(ns dinsro.components.forms.add-user-transaction
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
            [dinsro.spec.events.forms.add-user-transaction :as s.e.f.add-user-transaction]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]))

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.add-user-transaction/form-data])]
    (when @(rf/subscribe [::s.e.f.add-user-transaction/shown?])
      [:div
       [c/close-button ::s.e.f.add-user-transaction/set-shown?]
       [c.debug/debug-box form-data]
       [:p "Form"]
       [:div.field>div.control
        [c/number-input (tr [:value])
         ::s.e.f.create-transaction/value ::s.e.f.create-transaction/set-value]]
       [:div.field>div.control
        [c/currency-selector (tr [:currency])
         ::s.e.f.create-transaction/currency-id ::s.e.f.create-transaction/set-currency-id]]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::submit-clicked]]]])))
