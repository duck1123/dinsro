(ns dinsro.events.forms.create-rate
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.store :as st]))

(def default-rate 1)

(defn form-data-sub
  [{:keys [::s.e.f.create-rate/currency-id
           ::s.e.f.create-rate/date
           ::s.e.f.create-rate/rate]}
   _]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        (js/Date. date)})

(def form-data ::form-data)

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
    (st/reg-basic-sub ::s.e.f.create-rate/rate)
    (st/reg-set-event ::s.e.f.create-rate/rate)
    (st/reg-basic-sub ::s.e.f.create-rate/currency-id)
    (st/reg-set-event ::s.e.f.create-rate/currency-id)
    (st/reg-basic-sub ::s.e.f.create-rate/date)
    (st/reg-set-event ::s.e.f.create-rate/date)
    (st/reg-basic-sub ::s.e.f.create-rate/time)
    (st/reg-set-event ::s.e.f.create-rate/time)
    (st/reg-basic-sub ::shown?)
    (st/reg-set-event ::shown?)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::init-form init-form))
  store)
