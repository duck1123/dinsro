(ns dinsro.events.forms.create-transaction
  (:require [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.create-transaction/currency-id)
(rfu/reg-set-event ::s.e.f.create-transaction/currency-id)

(rfu/reg-basic-sub ::s.e.f.create-transaction/date)
(rfu/reg-set-event ::s.e.f.create-transaction/date)

(rfu/reg-basic-sub ::s.e.f.create-transaction/shown?)
(rfu/reg-set-event ::s.e.f.create-transaction/shown?)

(rfu/reg-basic-sub ::s.e.f.create-transaction/value)
(rfu/reg-set-event ::s.e.f.create-transaction/value)

(defn form-data-sub
  [[value currency-id date]]
  {:value value
   :currency-id (int currency-id)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-transaction/value]
 :<- [::s.e.f.create-transaction/currency-id]
 :<- [::s.e.f.create-transaction/date]
 form-data-sub)
