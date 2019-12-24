(ns dinsro.events.forms.add-user-transaction
  (:require [dinsro.spec.events.forms.add-user-transaction :as s.e.f.add-user-transaction]
            [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

;; toggleable

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

;; properties

(defn-spec form-data-sub ::s.e.f.add-user-transaction/form-data-output
  [[value currency-id] ::s.e.f.add-user-transaction/form-data-input
   _ any?]
  {:value value
   :currency-id (int currency-id)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-transaction/value]
 :<- [::s.e.f.create-transaction/currency-id]
 form-data-sub)
(def form-data ::form-data)
