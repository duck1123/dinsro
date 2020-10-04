(ns dinsro.events.forms.create-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu]
   [dinsro.spec.actions.transactions :as s.a.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.create-transaction)

(eu/declare-form
 ns-sym
 ::s.a.transactions/create-params-valid
 [::s.e.f.create-transaction/account-id
  ::s.e.f.create-transaction/date
  ::s.e.f.create-transaction/description
  ::s.e.f.create-transaction/value])

(s/def ::form-data-db (s/keys :req [::s.e.f.create-transaction/account-id
                                    ::s.e.f.create-transaction/date
                                    ::s.e.f.create-transaction/description
                                    ::s.e.f.create-transaction/value]))
(s/def ::form-data-event (s/cat :kw keyword?))
(s/def ::form-data ::s.a.transactions/create-params-valid)

(defn form-data-sub
  [{:keys [::s.e.f.create-transaction/account-id
           ::s.e.f.create-transaction/date
           ::s.e.f.create-transaction/description
           ::s.e.f.create-transaction/value]}
   _]
  {:account-id  (int account-id)
   :value       (.parseFloat js/Number value)
   :description description
   :date        date})

(s/fdef form-data-sub
  :args (s/cat :db ::form-data-db
               :event ::form-data-event)
  :ret ::form-data)

(defn init-handlers!
  [store]
  (doto store
    (eu/register-form ns-sym)
    (st/reg-sub ::form-data form-data-sub))
  store)
