(ns dinsro.components.forms.create-rate
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-rate 1)

(s/def ::rate string?)
(rfu/reg-basic-sub ::rate)
(rfu/reg-set-event ::rate)

(s/def ::currency-id string?)
(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(s/def ::date string?)
(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(s/def ::time string?)
(rfu/reg-basic-sub ::time)
(rfu/reg-set-event ::time)

(rfu/reg-basic-sub ::shown?)

(defn create-form-data
  [[currency-id rate date]]
  {:currency-id (int currency-id)
   :rate        (js/Number.parseFloat rate)
   :date        (js/Date. date #_(str date "T" time))})

(rf/reg-sub
 ::form-data
 :<- [::currency-id]
 :<- [::rate]
 :<- [::date]
 create-form-data)

(defn submit-clicked
  [_ _]
  {:dispatch [::e.rates/do-submit @(rf/subscribe [::form-data])]})

(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn toggle-form
  [db _]
  (update db ::shown? not))

(kf/reg-event-db ::toggle-form toggle-form)

(defn toggle-button
  []
  [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"])

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)
        date-string (str (.getFullYear default-date) "-" (.getMonth default-date) "-0" (.getDate default-date))
        time-string (str (.getHours default-date) ":" (.getMinutes default-date))]
    {:db (merge db {::rate (str default-rate)
                    ::currency-id ""
                    ::date date-string
                    ::time time-string})}))

(kf/reg-event-fx ::init-form init-form)

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::init-form]})

(defn create-rate-form
  []
  (let [shown? @(rf/subscribe [::shown?])
        form-data @(rf/subscribe [::form-data])]
    [:<>
     [toggle-button]
     [:a.button {:on-click #(rf/dispatch [::init-form])} "Init"]
     (when shown?
      [:<>
       [c/number-input      "Rate"     ::rate        ::set-rate]
       [c/input-field       "Date"     ::date        ::set-date :date]
       [c/input-field       "Time"     ::time        ::set-time :time]
       [c/currency-selector "Currency" ::currency-id ::set-currency-id]
       [c.debug/debug-box form-data]
       [c/primary-button    "Submit"   [::submit-clicked]]])]))
