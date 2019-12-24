(ns dinsro.events.forms.create-rate
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-rate 1)

(rfu/reg-basic-sub ::s.e.f.create-rate/rate)
(rfu/reg-set-event ::s.e.f.create-rate/rate)

(rfu/reg-basic-sub ::s.e.f.create-rate/currency-id)
(rfu/reg-set-event ::s.e.f.create-rate/currency-id)

(rfu/reg-basic-sub ::s.e.f.create-rate/date)
(rfu/reg-set-event ::s.e.f.create-rate/date)

(rfu/reg-basic-sub ::s.e.f.create-rate/time)
(rfu/reg-set-event ::s.e.f.create-rate/time)

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn form-data-sub
  [[currency-id rate date] _]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-rate/currency-id]
 :<- [::s.e.f.create-rate/rate]
 :<- [::s.e.f.create-rate/date]
 form-data-sub)
(def form-data ::form-data)

(defn-spec init-form (s/keys)
  [{:keys [db]} (s/keys) _ any?]
  (let [default-date (js/Date.)]
    {:db (merge db {::s.e.f.create-rate/rate (str default-rate)
                    ::s.e.f.create-rate/currency-id ""
                    ::s.e.f.create-rate/date (.toISOString default-date)})}))

(kf/reg-event-fx ::init-form init-form)
