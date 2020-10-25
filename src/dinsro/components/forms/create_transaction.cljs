(ns dinsro.components.forms.create-transaction
  (:require
   [dinsro.components :as c]
   [dinsro.components.datepicker :as c.datepicker]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn form
  [store]
  (let [form-data (st/subscribe store [::e.f.create-transaction/form-data])
        shown? (st/subscribe store [::e.f.create-transaction/shown?])]
    (when @shown?
      [:div
       [c/close-button store ::e.f.create-transaction/set-shown?]
       [:div.field-group
        [:div.field>div.column
         [:label.label (tr [:value])]
         [:input.input
          {:type :text
           :value @(st/subscribe store [::s.e.f.create-transaction/value])
           :on-change #(st/dispatch store [::s.e.f.create-transaction/set-value (c/target-value %)])}]]
        [:div.field>div.column
         ;; [:label.label (tr [:description])]
         [c/text-input store (tr [:description]) ::s.e.f.create-transaction/description]]
        [:div.field>div.column
         [:label.label (tr [:accounts])]
         [c/account-selector store (tr [:account]) ::s.e.f.create-transaction/account-id]]
        [:div.field>div.column
         [:label.label (tr [:date])]
         [c.datepicker/datepicker
          {:on-select #(st/dispatch store [::s.e.f.create-transaction/set-date %])}]]]
       [:div.field-group>div.field>div.column
        [c/primary-button store (tr [:submit]) [::e.transactions/do-submit @form-data]]]])))
