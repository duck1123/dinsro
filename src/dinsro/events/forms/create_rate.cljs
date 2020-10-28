(ns dinsro.events.forms.create-rate
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.utils :as eu]
   [dinsro.spec.actions.rates :as s.a.rates]
   [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(def ns-sym 'dinsro.events.forms.create-rate)

(eu/declare-form
 ns-sym
 ::s.a.rates/create-params-input
 [[:currency-id    ::s.e.f.create-rate/currency-id    0]
  [:date           ::s.e.f.create-rate/date           ""]
  [:rate-source-id ::s.e.f.create-rate/rate-source-id 1]
  [:rate           ::s.e.f.create-rate/rate           1]])

(def default-rate 1)

(s/def ::form-data ::s.a.rates/create-params-input)
(def form-data ::form-data)

(s/def ::form-data-db
  (s/keys :req [::s.e.f.create-rate/currency-id
                ::s.e.f.create-rate/date
                ::s.e.f.create-rate/rate]))
(s/def ::form-data-event (s/cat :kw keyword?))

(defn form-data-sub
  [{:keys [::s.e.f.create-rate/currency-id
           ::s.e.f.create-rate/date
           ::s.e.f.create-rate/rate]}
   _]
  (let [date-inst (or (and date (tick/instant date))
                      (tick/instant))]
    {:currency-id (int currency-id)
     :rate   (js/Number.parseFloat (or rate "0"))
     :date   (str date-inst)}))

(s/fdef form-data-sub
  :args (s/cat :db ::form-data-db
               :event ::form-data-event)
  :ret ::form-data)

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
