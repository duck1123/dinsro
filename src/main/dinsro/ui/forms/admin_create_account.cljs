(ns dinsro.ui.forms.admin-create-account
  (:require
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.specs.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [taoensso.timbre :as timbre]))

(defn form
  [store]
  (let [form-data @(st/subscribe store [::e.f.create-account/form-data])]
    (when @(st/subscribe store [::e.f.create-account/shown?])
      [:<>
       [u/close-button store ::e.f.create-account/set-shown?]
       [u/text-input store (tr [:name]) ::s.e.f.create-account/name]
       [u/number-input store (tr [:initial-value]) ::s.e.f.create-account/initial-value]
       [u/currency-selector store (tr [:currency]) ::s.e.f.create-account/currency-id]
       [u/user-selector store (tr [:user]) ::s.e.f.create-account/user-id]
       [u/primary-button store (tr [:submit]) [::e.accounts/do-submit form-data]]])))
