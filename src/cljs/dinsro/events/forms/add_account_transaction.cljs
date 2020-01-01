(ns dinsro.events.forms.add-account-transaction
  (:require [clojure.spec.alpha :as s]
            [dinsro.events.forms.create-transaction :as e.f.create-transaction]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(s/def ::shown? boolean?)
(def shown? ::shown?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn form-data-sub
  [params event]
  (let [[value date] params
        [_ account-id] event]
    (e.f.create-transaction/form-data-sub [account-id date value] event)))

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-transaction/value]
 :<- [::s.e.f.create-transaction/date]
 form-data-sub)
(def form-data ::form-data)
