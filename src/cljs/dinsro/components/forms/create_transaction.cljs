(ns dinsro.components.forms.create-transaction
  (:require
   [dinsro.components :as c]
   [dinsro.components.datepicker :as c.datepicker]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn form-shown
  [store form-data]
  [:div
   [c/close-button store ::e.f.create-transaction/set-shown?]
   [c.debug/debug-box store @form-data]
   [:div
    [:div.field-group
     [:div.field.is-inline-block-tablet
      [:label.label (tr [:value])]
      [:div.control
       [:input.input
        {:type :text
         :value @(st/subscribe store [::s.e.f.create-transaction/value])
         :on-change #(st/dispatch store [::s.e.f.create-transaction/set-value (c/target-value %)])}]]]]
    [:div.field-group
     [:div.column
      [c/account-selector store (tr [:account]) ::s.e.f.create-transaction/account-id]]
     [:div.column
      [:label.label (tr [:date])]
      [c.datepicker/datepicker
       {:on-select #(st/dispatch store [::s.e.f.create-transaction/set-date %])}]]]]
   [:div.field>div.control
    [c/primary-button store (tr [:submit]) [::e.transactions/do-submit @form-data]]]])

(defn form-inner
  [store form-data shown?]
  (when shown?
    [form-shown store form-data]))

(defn form
  [store]
  (let [form-data (st/subscribe store [::e.f.create-transaction/form-data])
        shown? (st/subscribe store [::e.f.create-transaction/shown?])]
    (form-inner store form-data shown?)))
