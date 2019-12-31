(ns dinsro.components.forms.add-currency-rate
  (:require [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.rates :as e.rates]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn form
  [currency-id]
  (when @(rf/subscribe [::shown?])
    (let [form-data (assoc @(rf/subscribe [::form-data]) :currency-id currency-id)]
      [:<>
       [c/close-button ::set-shown?]
       [:div.field>div.control
        [c/number-input (tr [:rate]) ::rate ::set-rate]]
       [:div.field>div.control
        [c.datepicker/datepicker
         {:on-select #(rf/dispatch [::set-date %])}]]
       [c.debug/debug-box form-data]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::e.rates/do-submit form-data]]]])))
