(ns dinsro.ui.forms.add-user-transaction
  (:require
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.specs.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.datepicker :as u.datepicker]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.inputs :as u.inputs]))

(defn form-shown
  [store form-data]
  (let [state @(st/subscribe store [::e.transactions/do-submit-state])]
    [:div
     [u.buttons/close-button store ::e.f.add-user-transaction/set-shown?]
     (when (= state :failed)
       [u.debug/error-message-box "There was an error submitting"])
     [:div.field>div.control
      [u.inputs/number-input store (tr [:value]) ::s.e.f.create-transaction/value]]
     [:div.field>div.control
      [u.inputs/account-selector store (tr [:account]) ::s.e.f.create-transaction/account-id]]
     [:div.field>div.control
      [:label.label (tr [:date])]
      [u.datepicker/datepicker
       {:on-select #(st/dispatch store [::s.e.f.create-transaction/set-date %])}]]
     [:div.field>div.control
      [u.inputs/primary-button store (tr [:submit]) [::e.transactions/do-submit form-data]]]]))

(defn form
  [store]
  ;; TODO: get account id
  (let [form-data @(st/subscribe store [::e.f.add-user-transaction/form-data 1])]
    (when @(st/subscribe store [::e.f.add-user-transaction/shown?])
      [form-shown store form-data])))
