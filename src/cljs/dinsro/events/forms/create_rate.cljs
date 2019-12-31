(ns dinsro.events.forms.create-rate
  (:require [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-rate 1)

(rfu/reg-basic-sub ::rate)
(rfu/reg-set-event ::rate)

(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(rfu/reg-basic-sub ::time)
(rfu/reg-set-event ::time)

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn form-data-sub
  [[currency-id rate date] _]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::currency-id]
 :<- [::rate]
 :<- [::date]
 form-data-sub)

(defn toggle-form
  [{:keys [db]} _]
  {:db (update db ::shown? not)})

(kf/reg-event-fx ::toggle-form toggle-form)

(defn toggle-button
  []
  [:a.button {:on-click #(rf/dispatch [::toggle-form])} (tr [:toggle])])

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)]
    {:db (merge db {::rate (str default-rate)
                    ::currency-id ""
                    ::date (.toISOString default-date)})}))

(kf/reg-event-fx ::init-form init-form)
