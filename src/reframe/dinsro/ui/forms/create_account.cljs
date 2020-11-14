(ns dinsro.ui.forms.create-account
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.specs.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defn form
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-account/form-data])]
    (when @(st/subscribe store [::e.f.create-account/shown?])
      [:div
       [u.buttons/close-button store ::e.f.create-account/set-shown?]
       [u.inputs/text-input store (tr [:name]) ::s.e.f.create-account/name]
       [u.inputs/number-input store (tr [:initial-value]) ::s.e.f.create-account/initial-value]
       [u.inputs/currency-selector store (tr [:currency]) ::s.e.f.create-account/currency-id]
       [u.inputs/user-selector store (tr [:user]) ::s.e.f.create-account/user-id]
       [u.inputs/primary-button store (tr [:submit]) [::e.accounts/do-submit form-data]]])))
