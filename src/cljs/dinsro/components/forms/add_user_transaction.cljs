(ns dinsro.components.forms.add-user-transaction
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]))

(defn form
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:div
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [:p "Form"]
       [:div.field>div.control
        [c/number-input (tr [:value]) ::value ::set-value]]
       [:div.field>div.control
        [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::submit-clicked]]]])))
