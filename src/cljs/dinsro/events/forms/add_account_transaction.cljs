(ns dinsro.events.forms.add-account-transaction
  (:require [dinsro.spec.events.forms.add-account-transaction :as s.e.f.add-account-transaction]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.add-account-transaction/shown?)
(rfu/reg-set-event ::s.e.f.add-account-transaction/shown?)

(rfu/reg-basic-sub ::s.e.f.add-account-transaction/currency-id)
(rfu/reg-set-event ::s.e.f.add-account-transaction/currency-id)

(rfu/reg-basic-sub ::s.e.f.add-account-transaction/date)
(rfu/reg-set-event ::s.e.f.add-account-transaction/date)

(rfu/reg-basic-sub ::s.e.f.add-account-transaction/value)
(rfu/reg-set-event ::s.e.f.add-account-transaction/value)

(defn form-data-sub
  [params]
  (let [[value currency-id date] params]
    {:value       (.parseFloat js/Number value)
     :currency-id (int currency-id)
     :date        date}))

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.add-account-transaction/value]
 :<- [::s.e.f.add-account-transaction/currency-id]
 :<- [::s.e.f.add-account-transaction/date]
 form-data-sub)
