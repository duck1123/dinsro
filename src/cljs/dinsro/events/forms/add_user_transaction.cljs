(ns dinsro.events.forms.add-user-transaction
  (:require [dinsro.spec.events.forms.add-user-transaction :as s.e.f.add-user-transaction]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::s.e.f.add-user-transaction/shown?)
(rfu/reg-set-event ::s.e.f.add-user-transaction/shown?)

(rfu/reg-basic-sub ::s.e.f.add-user-transaction/currency-id)
(rfu/reg-set-event ::s.e.f.add-user-transaction/currency-id)

(rfu/reg-basic-sub ::s.e.f.add-user-transaction/date)
(rfu/reg-set-event ::s.e.f.add-user-transaction/date)

(rfu/reg-basic-sub ::s.e.f.add-user-transaction/value)
(rfu/reg-set-event ::s.e.f.add-user-transaction/value)

(defn-spec form-data-sub ::s.e.f.add-user-transaction/form-data-output
  [[value currency-id] ::s.e.f.add-user-transaction/form-data-input
   _ any?]
  {:value value
   :currency-id (int currency-id)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.add-user-transaction/value]
 :<- [::s.e.f.add-user-transaction/currency-id]
 form-data-sub)
