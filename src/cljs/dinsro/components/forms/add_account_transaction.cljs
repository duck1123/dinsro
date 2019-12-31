(ns dinsro.components.forms.add-account-transaction
  (:require [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.add-account-transaction :as e.f.add-account-transaction]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn form
  [id]
  [:<>
   (let [form-data (assoc @(rf/subscribe [::e.f.add-account-transaction/form-data])
                          :account-id id)]
     (when @(rf/subscribe [::s.e.f.add-account-transaction/shown?])
       [:div
        [c/close-button ::s.e.f.add-account-transaction/set-shown?]
        [c.debug/debug-box form-data]
        [:div.field>div.control
         [c/number-input (tr [:value])
          ::s.e.f.add-account-transaction/value ::s.e.f.add-account-transaction/set-value]]
        [:div.field>div.control
         [c/currency-selector (tr [:currency])
          ::s.e.f.add-account-transaction/currency-id ::s.e.f.add-account-transaction/set-currency-id]]
        [:div.field>div.control
         [c.datepicker/datepicker
          {:on-select #(rf/dispatch [::s.e.f.add-account-transaction/set-date %])}]]
        [:div.field>div.control
         [c/primary-button (tr [:submit]) [::e.transactions/do-submit form-data]]]]))])
