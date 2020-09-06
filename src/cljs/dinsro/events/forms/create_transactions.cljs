(ns dinsro.events.forms.create-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.actions.transactions :as s.a.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]))

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
           ::s.e.f.create-transaction/value
           ]}
   _]
  {:account-id  (int account-id)
   :value       (.parseFloat js/Number value)
   :description description
   :date        date})

(s/fdef form-data-sub
  :args (s/cat :db ::form-data-db
               :event ::form-data-event)
  :ret ::form-data)

(rf/reg-sub ::form-data form-data-sub)
(def form-data ::form-data)
