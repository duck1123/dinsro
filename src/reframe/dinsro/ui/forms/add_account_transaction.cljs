(ns dinsro.ui.forms.add-account-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.forms.add-account-transaction :as e.f.add-account-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.specs :as ds]
   [dinsro.specs.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [dinsro.ui.datepicker :as u.datepicker]
   [taoensso.timbre :as timbre]))

(defn form
  [store id]
  (let [form-data @(st/subscribe store [::e.f.add-account-transaction/form-data id])]
    (when @(st/subscribe store [::e.f.add-account-transaction/shown?])
      [:div
       [u/close-button store ::e.f.add-account-transaction/set-shown?]
       [:div.field>div.control
        [u/text-input store (tr [:description]) ::s.e.f.create-transaction/description]]
       [:div.field>div.control
        [u/number-input store (tr [:value]) ::s.e.f.create-transaction/value]]
       [:div.field>div.control
        [u.datepicker/datepicker {:on-select #(st/dispatch store [::s.e.f.create-transaction/set-date %])}]]
       [:div.field>div.control
        [u/primary-button store (tr [:submit]) [::e.transactions/do-submit form-data]]]])))

(s/fdef form
  :args (s/cat :id ::ds/id)
  :ret vector?)
