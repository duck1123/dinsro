(ns dinsro.events.forms.add-account-transaction
  (:require [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn form-data-sub
  [params event]
  (let [[value currency-id date] params]
    (e.f.create-transaction/form-data-sub [53 value currency-id date] event)))

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-transaction/value]
 :<- [::s.e.f.create-transaction/currency-id]
 :<- [::s.e.f.create-transaction/date]
 form-data-sub)
(def form-data ::form-data)
