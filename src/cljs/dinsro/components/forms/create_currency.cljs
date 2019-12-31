(ns dinsro.components.forms.create-currency
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]))

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::set-defaults]})

(defn form
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:<>
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [:form
        [c/text-input     (tr [:name])   ::name ::set-name]
        [c/primary-button (tr [:submit]) [::e.currencies/do-submit form-data]]]])))
