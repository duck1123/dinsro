(ns dinsro.ui.forms.create-transaction
  (:require
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.specs.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.datepicker :as u.datepicker]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defn form
  [store]
  (let [form-data (st/subscribe store [::e.f.create-transaction/form-data])
        shown? (st/subscribe store [::e.f.create-transaction/shown?])]
    (when @shown?
      [:div
       [u.buttons/close-button store ::e.f.create-transaction/set-shown?]
       [:div.field-group
        [:div.field>div.column
         [:label.label (tr [:value])]
         [:input.input
          {:type :text
           :value @(st/subscribe store [::s.e.f.create-transaction/value])
           :on-change #(st/dispatch store [::s.e.f.create-transaction/set-value (u.inputs/target-value %)])}]]
        [:div.field>div.column
         ;; [:label.label (tr [:description])]
         [u.inputs/text-input store (tr [:description]) ::s.e.f.create-transaction/description]]
        [:div.field>div.column
         [:label.label (tr [:accounts])]
         [u.inputs/account-selector store (tr [:account]) ::s.e.f.create-transaction/account-id]]
        [:div.field>div.column
         [:label.label (tr [:date])]
         [u.datepicker/datepicker
          {:on-select #(st/dispatch store [::s.e.f.create-transaction/set-date %])}]]]
       [:div.field-group>div.field>div.column
        [u.inputs/primary-button store (tr [:submit]) [::e.transactions/do-submit @form-data]]]])))
