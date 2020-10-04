(ns dinsro.events.forms.create-rate
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.create-rate)

(eu/declare-form
 ns-sym
 [::s.e.f.create-rate/currency-id
  ::s.e.f.create-rate/date
  ::s.e.f.create-rate/rate])

(def default-rate 1)

(defn form-data-sub
  [{:keys [::s.e.f.create-rate/currency-id
           ::s.e.f.create-rate/date
           ::s.e.f.create-rate/rate]}
   _]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        (js/Date. date)})

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)]
    {:db (merge db {::s.e.f.create-rate/rate (str default-rate)
                    ::s.e.f.create-rate/currency-id ""
                    ::s.e.f.create-rate/date (.toISOString default-date)})}))

(s/fdef init-form
  :args (s/cat :cofx any?
               :event any?)
  :ret (s/keys))

(defn init-handlers!
  [store]
  (doto store
    (eu/register-form ns-sym)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::init-form init-form))
  store)
