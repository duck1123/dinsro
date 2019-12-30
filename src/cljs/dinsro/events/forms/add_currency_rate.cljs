(ns dinsro.events.forms.add-currency-rate
  (:require [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::valid)

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(rfu/reg-basic-sub ::time)
(rfu/reg-set-event ::time)

(rfu/reg-basic-sub ::rate)
(rfu/reg-set-event ::rate)

(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(defn create-form-data
  [[currency-id rate date time]]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        (js/Date. (str date "T" time))})

(rf/reg-sub
 ::form-data
 :<- [::currency-id]
 :<- [::rate]
 :<- [::date]
 c.f.create-rate/create-form-data)

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)
        default-opts {
                      ;; ::shown? false
                      ::rate (str default-rate)
                      ::date (.toISOString default-date)}]
    {:db (merge db default-opts)}))

(kf/reg-event-fx ::init-form init-form)

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::init-form]})
