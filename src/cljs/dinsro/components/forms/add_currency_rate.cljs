(ns dinsro.components.forms.add-currency-rate
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::valid boolean?)
(rfu/reg-basic-sub ::valid)

(rfu/reg-basic-sub ::shown?)

(def default-rate 1)

(s/def ::date string?)
(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(s/def ::time string?)
(rfu/reg-basic-sub ::time)
(rfu/reg-set-event ::time)

(s/def ::rate string?)
(rfu/reg-basic-sub ::rate)
(rfu/reg-set-event ::rate)

(s/def ::currency-id string?)
(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::shown? not)}))

(kf/reg-event-fx ::toggle toggle)

(defn submit-clicked
  [cofx event]
  (let [[data] event]
    {:dispatch [::e.rates/do-submit data]}))

(s/def ::add-currency-rate-form
  (s/keys :req-un [::s.rates/date ::s.rates/rate ::s.rates/currency-id]))

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
 :<- [::time]
 create-form-data)

(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn toggle-button
  []
  (let [shown? @(rf/subscribe [::shown?])]
    [:a {:on-click #(rf/dispatch [::toggle])}
     (if shown?
       [:span.icon>i.fas.fa-chevron-down]
       [:span.icon>i.fas.fa-chevron-right])]))

(defn init-form
  [{:keys [db]} _]
  (let [default-date (js/Date.)
        date-string (str (.getFullYear default-date) "-" (inc (.getMonth default-date)) "-0" (.getDate default-date))
        time-string (str (.getHours default-date) ":" (.getMinutes default-date))]
    {:db (merge db {::rate (str default-rate)
                    ::date date-string
                    ::time time-string})}))

(kf/reg-event-fx ::init-form init-form)

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::init-form]})

(defn-spec add-currency-rate-form vector?
  [currency-id :db/id]
  (let [shown? @(rf/subscribe [::shown?])]
    [:<>
     [:div [toggle-button]]
     [:a.button {:on-click #(rf/dispatch [::init-form])} "Init"]
     (when shown?
       (let [form-data (assoc @(rf/subscribe [::form-data]) :currency-id currency-id)]
         [:<>
          [c/number-input (tr [:rate]) ::rate ::set-rate]
          [c/input-field (tr [:date]) ::date ::set-date :date]
          [c/input-field (tr [:time]) ::time ::set-time :time]
          #_[:p "Currency Id: " currency-id]
          #_[c/currency-selector "Currency"  ::currency-id ::change-currency-id]
          [c.debug/debug-box form-data]
          [c/primary-button (tr [:submit]) [::submit-clicked form-data]]]))]))
