(ns dinsro.components.forms.add-account-transaction
  (:require [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.transactions :as e.transactions]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn-spec form vector?
  []
  [:<>
   (let [form-data @(rf/subscribe [::form-data])]
     (when @(rf/subscribe [::shown?])
       [:div
        [c/close-button ::set-shown?]
        [c.debug/debug-box form-data]
        [:div.field>div.control
         [c/number-input (tr [:value]) ::value ::set-value]]
        [:div.field>div.control
         [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]]
        [:div.field>div.control
         [c.datepicker/datepicker
          {:on-select #(rf/dispatch [::set-date %])}]]
        [:div.field>div.control
         [c/primary-button (tr [:submit]) [::e.transactions/do-submit form-data]]]]))])
