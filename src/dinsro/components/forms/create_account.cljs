(ns dinsro.components.forms.create-account
  (:require
   [dinsro.components :as c]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn form
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-account/form-data])]
    (when @(st/subscribe store [::e.f.create-account/shown?])
      [:<>
       [c/close-button store ::e.f.create-account/set-shown?]
       [c/text-input store (tr [:name]) ::s.e.f.create-account/name]
       [c/number-input store (tr [:initial-value]) ::s.e.f.create-account/initial-value]
       [c/currency-selector store (tr [:currency]) ::s.e.f.create-account/currency-id]
       [c/user-selector store (tr [:user]) ::s.e.f.create-account/user-id]
       [c/primary-button store (tr [:submit]) [::e.accounts/do-submit form-data]]])))
