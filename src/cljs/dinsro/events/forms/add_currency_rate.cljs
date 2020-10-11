(ns dinsro.events.forms.add-currency-rate
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu]
   [dinsro.events.forms.create-rate :as e.f.create-rate]
   [dinsro.spec :as ds]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.add-currency-rate)

(eu/declare-subform
 ns-sym
 [::s.e.f.create-rate/date
  ::s.e.f.create-rate/rate])

(s/def ::form-data-db (s/keys :req [::s.e.f.create-rate/date
                                    ::s.e.f.create-rate/rate]))
(s/def ::form-data-event (s/cat :kw keyword? :id ::ds/id-string))

(defn form-data-sub
  [{:keys [::s.e.f.create-rate/date
           ::s.e.f.create-rate/rate]}
   [_ currency-id]]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        date})

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)
        default-opts {::s.e.f.create-rate/rate (str e.f.create-rate/default-rate)
                      ::s.e.f.create-rate/date (.toISOString default-date)}]
    {:db (merge db default-opts)}))

(defn init-handlers!
  [store]
  (doto store
    (eu/register-subform ns-sym)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::init-form init-form))
  store)
