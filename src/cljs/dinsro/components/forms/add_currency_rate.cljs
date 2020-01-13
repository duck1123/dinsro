(ns dinsro.components.forms.add-currency-rate
  (:require
   [dinsro.components :as c]
   [dinsro.components.datepicker :as c.datepicker]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.forms.add-currency-rate :as e.f.add-currency-rate]
   [dinsro.events.rates :as e.rates]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::e.f.add-currency-rate/init-form]})

(defn form
  [currency-id]
  (when @(rf/subscribe [::e.f.add-currency-rate/shown?])
    (let [form-data @(rf/subscribe [::e.f.add-currency-rate/form-data currency-id])]
      [:<>
       [c/close-button ::e.f.add-currency-rate/set-shown?]
       [:div.field>div.control
        [c/number-input (tr [:rate]) ::s.e.f.create-rate/rate]]
       [:div.field>div.control
        [c.datepicker/datepicker {:on-select #(rf/dispatch [::s.e.f.create-rate/set-date %])}]]
       [c.debug/debug-box form-data]
       [:div.field>div.control
        [c/primary-button (tr [:submit]) [::e.rates/do-submit form-data]]]])))
