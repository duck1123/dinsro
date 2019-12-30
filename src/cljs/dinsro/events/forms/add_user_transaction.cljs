(ns dinsro.events.forms.add-user-transaction
  (:require [clojure.spec.alpha :as s]
            [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.spec.actions.transactions :as s.a.transactions]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn-spec form-data-sub ::s.a.transactions/create-params-valid
  [[currency-id date value] any?
   event any?]
  (let [[_ account-id] event]
    (e.f.create-transaction/form-data-sub [account-id currency-id date value] event)))

(s/def ::form-data ::s.a.transactions/create-params-valid)
(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-transaction/currency-id]
 :<- [::s.e.f.create-transaction/date]
 :<- [::s.e.f.create-transaction/value]
 form-data-sub)
(def form-data ::form-data)
