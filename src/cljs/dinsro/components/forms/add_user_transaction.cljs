(ns dinsro.components.forms.add-user-transaction
  (:require [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]))

(defn form-shown
  [form-data]
  [:div
   [c/close-button ::e.f.add-user-transaction/set-shown?]
   [c.debug/debug-box form-data]
   [:div.field>div.control
    [c/number-input (tr [:value])
     ::s.e.f.create-transaction/value ::s.e.f.create-transaction/set-value]]

   ;; TODO: restrict to user's accounts
   [:div.field>div.control
    [c/account-selector (tr [:account])
     ::s.e.f.create-transaction/account-id ::s.e.f.create-transaction/set-account-id]]
   [:div.field>div.control
    [c/currency-selector (tr [:currency])
     ::s.e.f.create-transaction/currency-id ::s.e.f.create-transaction/set-currency-id]]
   [:div.field>div.control
    [:label.label (tr [:date])]
    [c.datepicker/datepicker {:on-select #(rf/dispatch [::s.e.f.create-transaction/set-date %])}]]
   [:div.field>div.control
    [c/primary-button (tr [:submit]) [::e.transactions/do-submit form-data]]]])

(defn form
  []
  ;; TODO: get account id
  (let [form-data @(rf/subscribe [::e.f.add-user-transaction/form-data 1])]
    (when @(rf/subscribe [::e.f.add-user-transaction/shown?])
      [form-shown form-data])))
