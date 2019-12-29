(ns dinsro.events.forms.create-transaction
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.actions.transactions :as s.a.transactions]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

;; toggleable

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

;; properties

(rfu/reg-basic-sub ::s.e.f.create-transaction/account-id)
(rfu/reg-set-event ::s.e.f.create-transaction/account-id)

(rfu/reg-basic-sub ::s.e.f.create-transaction/date)
(rfu/reg-set-event ::s.e.f.create-transaction/date)

(rfu/reg-basic-sub ::s.e.f.create-transaction/description)
(rfu/reg-set-event ::s.e.f.create-transaction/description)

(rfu/reg-basic-sub ::s.e.f.create-transaction/value)
(rfu/reg-set-event ::s.e.f.create-transaction/value)

(defn form-data-sub
  [[account-id date value]
   _]
  {:account-id  (int account-id)
   :value       (.parseFloat js/Number value)
   :date        date})

(s/def ::form-data ::s.a.transactions/create-params-valid)
(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-transaction/account-id]
 :<- [::s.e.f.create-transaction/date]
 :<- [::s.e.f.create-transaction/description]
 :<- [::s.e.f.create-transaction/value]
 form-data-sub)
(def form-data ::form-data)
