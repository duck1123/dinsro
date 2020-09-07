(ns dinsro.components.forms.add-user-transaction
  (:require
   [dinsro.components :as c]
   [dinsro.components.datepicker :as c.datepicker]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]))

(defn form-shown
  [store form-data]
  (let [state (st/subscribe store [::e.transactions/do-submit-state])]
    [:div
     [c/close-button store ::e.f.add-user-transaction/set-shown?]
     (when (= @state :failed)
       [c/error-message-box "There was an error submitting"])
     [:p (str @state)]
     [:div.field>div.control
      [c/number-input store (tr [:value]) ::s.e.f.create-transaction/value]]
     [:div.field>div.control
      [:label.label>div.control (tr [:account])]
      [c/account-selector store (tr [:account]) ::s.e.f.create-transaction/account-id]]
     [:div.field>div.control
      [:label.label (tr [:date])]
      [c.datepicker/datepicker
       {:on-select #(st/dispatch store [::s.e.f.create-transaction/set-date %])}]]
     [:div.field>div.control
      [c/primary-button store (tr [:submit]) [::e.transactions/do-submit form-data]]]]))

(defn form
  [store]
  ;; TODO: get account id
  (let [form-data @(st/subscribe store [::e.f.add-user-transaction/form-data 1])]
    (when @(st/subscribe store [::e.f.add-user-transaction/shown?])
      [form-shown store form-data])))
