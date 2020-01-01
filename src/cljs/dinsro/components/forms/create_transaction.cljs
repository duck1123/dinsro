(ns dinsro.components.forms.create-transaction
  (:require [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn form-shown
  [form-data]
  [:div
   [c/close-button ::e.f.create-transaction/set-shown?]
   [c.debug/debug-box @form-data]
   [:div
    [:div.field-group
     [:div.field.is-inline-block-tablet
      [:label.label (tr [:value])]
      [:div.control
       [:input.input
        {:type :text
         :value @(rf/subscribe [::s.e.f.create-transaction/value])
         :on-change #(rf/dispatch [::s.e.f.create-transaction/set-value (c/target-value %)])}]]]
     #_[:div.field.is-inline-block-tablet
      [c/currency-selector (tr [:currency]) ::s.e.f.create-transaction/currency-id]]]
    [:div.field-group
     [:div.column
      [c/account-selector (tr [:account]) ::s.e.f.create-transaction/account-id]]
     [:div.column
      [:label.label (tr [:date])]
      [c.datepicker/datepicker
       {:on-select #(rf/dispatch [::s.e.f.create-transaction/set-date %])}]]]]
   [:div.field>div.control
    [c/primary-button (tr [:submit]) [::e.transactions/do-submit @form-data]]]])

(defn form-inner
  [form-data shown?]
  (when shown?
    [form-shown form-data]))

(defn form
  []
  (let [form-data (rf/subscribe [::e.f.create-transaction/form-data])
        shown? (rf/subscribe [::e.f.create-transaction/shown?])]
    (form-inner form-data shown?)))
