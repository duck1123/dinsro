(ns dinsro.components.forms.create-account
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn-spec form vector?
  []
  (let [form-data @(rf/subscribe [::form-data])]
    (when @(rf/subscribe [::shown?])
      [:<>
       [c/close-button ::set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::name ::set-name]
       [c/number-input (tr [:initial-value]) ::initial-value ::set-initial-value]
       [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]
       [c/user-selector (tr [:user]) ::user-id ::set-user-id]
       [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])))
