(ns dinsro.components.forms.create-account
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.forms.create-account :as e.f.create-account]
            [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]))

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.create-account/form-data])]
    (when @(rf/subscribe [::s.e.f.create-account/shown?])
      [:<>
       [c/close-button ::s.e.f.create-account/set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name])
        ::s.e.f.create-account/name ::s.e.f.create-account/set-name]
       [c/number-input (tr [:initial-value])
        ::s.e.f.create-account/initial-value ::s.e.f.create-account/set-initial-value]
       [c/currency-selector (tr [:currency])
        ::s.e.f.create-account/currency-id ::s.e.f.create-account/set-currency-id]
       [c/user-selector (tr [:user])
        ::s.e.f.create-account/user-id ::s.e.f.create-account/set-user-id]
       [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])))
