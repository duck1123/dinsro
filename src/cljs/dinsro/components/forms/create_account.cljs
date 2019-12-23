(ns dinsro.components.forms.create-account
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.spec.events.forms.create-accounts :as s.e.f.create-accounts]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]))

(defn form
  []
  (let [form-data @(rf/subscribe [::s.e.f.create-accounts/form-data])]
    (when @(rf/subscribe [::s.e.f.create-accounts/shown?])
      [:<>
       [c/close-button ::s.e.f.create-accounts/set-shown?]
       [c.debug/debug-box form-data]
       [c/text-input (tr [:name])
        ::s.e.f.create-accounts/name ::s.e.f.create-accounts/set-name]
       [c/number-input (tr [:initial-value])
        ::s.e.f.create-accounts/initial-value ::s.e.f.create-accounts/set-initial-value]
       [c/currency-selector (tr [:currency])
        ::s.e.f.create-accounts/currency-id ::s.e.f.create-accounts/set-currency-id]
       [c/user-selector (tr [:user])
        ::s.e.f.create-accounts/user-id ::s.e.f.create-accounts/set-user-id]
       [c/primary-button (tr [:submit]) [::e.accounts/do-submit form-data]]])))
