(ns dinsro.components.forms.add-user-account
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.forms.add-user-account :as e.f.add-user-account]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]))

(defn-spec form vector?
  [id ::ds/id]
  (let [form-data (assoc @(rf/subscribe [::e.f.add-user-account/form-data]) :user-id id)]
    (when @(rf/subscribe [::e.f.add-user-account/shown?])
      [:<>
       [c/close-button ::e.f.add-user-account/set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name])
        ::e.f.add-user-account/name ::e.f.add-user-account/set-name]
       [c/number-input (tr [:initial-value])
        ::e.f.add-user-account/initial-value ::e.f.add-user-account/set-initial-value]
       [c/currency-selector (tr [:currency])
        ::e.f.add-user-account/currency-id ::e.f.add-user-account/set-currency-id]
       [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])))
