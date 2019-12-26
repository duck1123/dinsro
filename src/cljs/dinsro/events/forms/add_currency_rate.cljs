(ns dinsro.events.forms.add-currency-rate
  (:require [dinsro.events.forms.create-rate :as e.f.create-rate]
            [dinsro.spec.events.forms.create-rate :as s.e.f.create-rate]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(rfu/reg-basic-sub ::s.e.f.create-rate/time)
(rfu/reg-set-event ::s.e.f.create-rate/time)

(rfu/reg-basic-sub ::s.e.f.create-rate/rate)
(rfu/reg-set-event ::s.e.f.create-rate/rate)

(rfu/reg-basic-sub ::s.e.f.create-rate/currency-id)
(rfu/reg-set-event ::s.e.f.create-rate/currency-id)

(defn form-data-sub
  [[rate date] [_ currency-id]]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        date #_(js/Date. (str date "T" time))})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-rate/rate]
 :<- [::s.e.f.create-rate/date]
 form-data-sub)
(def form-data ::form-data)

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)
        default-opts {::s.e.f.create-rate/rate (str e.f.create-rate/default-rate)
                      ::s.e.f.create-rate/date (.toISOString default-date)}]
    {:db (merge db default-opts)}))

(kf/reg-event-fx ::init-form init-form)
