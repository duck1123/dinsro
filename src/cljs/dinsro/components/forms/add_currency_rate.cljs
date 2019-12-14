(ns dinsro.components.forms.add-currency-rate
  (:require [cljc.java-time.instant :as instant]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.create-rate :as c.f.create-rate]
            [dinsro.components.datepicker :as c.datepicker]
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

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

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
 c.f.create-rate/create-form-data)

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

(defn-spec add-currency-rate-form vector?
  [currency-id any?]
  (let [shown? @(rf/subscribe [::shown?])]
    [:<>
     (when-not shown? [:div.is-pulled-right [toggle-button]])
     #_[:a.button {:on-click #(rf/dispatch [::init-form])} "Init"]
     (when shown?
       (let [form-data (assoc @(rf/subscribe [::form-data]) :currency-id currency-id)]
         [:<>
          [:a.delete.is-pulled-right {:on-click #(rf/dispatch [::set-shown? false])}]
          [:div.field>div.control
           [c/number-input (tr [:rate]) ::rate ::set-rate]]
          [:div.field>div.control
           [c.datepicker/datepicker
            {:on-select #(rf/dispatch [::set-date (timbre/spy :info %)])}]]
          [c.debug/debug-box form-data]
          [:div.field>div.control
           [c/primary-button (tr [:submit]) [::e.rates/do-submit form-data]]]]))]))
