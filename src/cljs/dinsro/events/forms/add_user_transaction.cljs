(ns dinsro.events.forms.add-user-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.spec.actions.transactions :as s.a.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn form-data-sub
  [[date value description] event]
  (let [[_ account-id] event]
    (e.f.create-transaction/form-data-sub
     [account-id date description value]
     event)))

(s/fdef form-data-sub
  :ret ::s.a.transactions/create-params-valid)

(s/def ::form-data ::s.a.transactions/create-params-valid)
(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-transaction/date]
 :<- [::s.e.f.create-transaction/value]
 :<- [::s.e.f.create-transaction/description]
 form-data-sub)
(def form-data ::form-data)
