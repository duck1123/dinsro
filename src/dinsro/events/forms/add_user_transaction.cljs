(ns dinsro.events.forms.add-user-transaction
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.spec.actions.transactions :as s.a.transactions]
   [dinsro.spec.events.forms.create-transaction :as s.e.f.create-transaction]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.add-user-transaction)

(eu/declare-subform
 ns-sym
 ::s.a.transactions/create-params-valid
 [[:date        ::s.e.f.create-transaction/date ""]
  [:description ::s.e.f.create-transaction/description ""]
  [:value ::s.e.f.create-transaction/value 0]])

(s/def ::form-data-db (s/keys :req [::s.e.f.create-transaction/date
                                    ::s.e.f.create-transaction/description
                                    ::s.e.f.create-transaction/value]))
(s/def ::form-data-event (s/cat :kw keyword?
                                :account-id :db/id))
(s/def ::form-data ::s.a.transactions/create-params-valid)

(defn form-data-sub
  [db event]
  (let [[_ account-id] event
        updated-db (-> db
                       (select-keys [::s.e.f.create-transaction/date
                                     ::s.e.f.create-transaction/description
                                     ::s.e.f.create-transaction/value])
                       (assoc ::s.e.f.create-transaction/account-id account-id))]
    (e.f.create-transaction/form-data-sub updated-db event)))

(s/fdef form-data-sub
  :args (s/cat :db ::form-data-db
               :event ::form-data-event)
  :ret ::form-data)

(defn init-handlers!
  [store]
  (doto store
    (eu/register-subform ns-sym)
    (st/reg-sub ::form-data form-data-sub))
  store)