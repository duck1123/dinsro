(ns dinsro.components.forms.add-user-account
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.forms.add-user-account :as e.f.add-user-account]
            [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]))

(defn form
  [id]
  (let [form-data (assoc @(rf/subscribe [::e.f.add-user-account/form-data]) :user-id id)]
    (when @(rf/subscribe [::e.f.add-user-account/shown?])
      [:<>
       [c/close-button ::e.f.add-user-account/set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name]) ::s.e.f.create-account/name]
       [c/number-input (tr [:initial-value]) ::s.e.f.create-account/initial-value]
       [c/currency-selector (tr [:currency]) ::s.e.f.create-account/currency-id]
       [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])))
