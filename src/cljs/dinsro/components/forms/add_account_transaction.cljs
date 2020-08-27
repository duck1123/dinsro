(ns dinsro.components.forms.add-account-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.datepicker :as c.datepicker]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.forms.add-account-transaction :as e.f.add-account-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn form
  [store id]
  (let [form-data @(st/subscribe store [::e.f.add-account-transaction/form-data id])]
    (when @(st/subscribe store [::e.f.add-account-transaction/shown?])
      [:div
       [c/close-button store ::e.f.add-account-transaction/set-shown?]
       [c.debug/debug-box store form-data]
       [:div.field>div.control
        [c/text-input store (tr [:description]) ::s.e.f.create-transaction/description]]
       [:div.field>div.control
        [c/number-input store (tr [:value]) ::s.e.f.create-transaction/value]]
       [:div.field>div.control
        [c.datepicker/datepicker {:on-select #(st/dispatch store [::s.e.f.create-transaction/set-date %])}]]
       [:div.field>div.control
        [c/primary-button store (tr [:submit]) [::e.transactions/do-submit form-data]]]])))

(s/fdef form
  :args (s/cat :id ::ds/id)
  :ret vector?)
