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

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.create-transaction/form-data])]
    (when @(rf/subscribe [::e.f.create-transaction/shown?])
      [:div
       [c/close-button ::e.f.create-transaction/set-shown?]
       [c.debug/debug-box form-data]
       [:div.columns.is-multiline
        [:div.column
         [c/number-input (tr [:value]) ::s.e.f.create-transaction/value]]
        [:div.column
         [c/currency-selector (tr [:currency]) ::s.e.f.create-transaction/currency-id]]]
       [:div.columns
        [:div.column
         [:div {:style {:display "inline-block"}}
          [c/account-selector (tr [:account]) ::s.e.f.create-transaction/account-id]]
         [:div {:style {:display :inline-block}}
          [c.datepicker/datepicker
           {:on-select #(rf/dispatch [::s.e.f.create-transaction/set-date %])}]]]
        [:div.column
         [:label.label (tr [:date])]]]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::e.transactions/do-submit form-data]]]])))
