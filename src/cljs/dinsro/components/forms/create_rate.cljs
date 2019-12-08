(ns dinsro.components.forms.create-rate
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
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

(rf/reg-sub
 ::form-data
 :<- [::currency-id]
 :<- [::rate]
 :<- [::date]
 :<- [::time]
 (fn-traced [[currency-id rate date time]]
   {:currency-id currency-id
    :rate        rate
    :date        date
    :time        time}))

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

(defn create-rate-form
  []
  (let [shown? @(rf/subscribe [::shown?])
        form-data @(rf/subscribe [::form-data])]
    [:<>
     [toggle-button]
     (when shown?
      [:div.box
       [c/number-input      "Rate"     ::rate        ::set-rate]
       [c/input-field       "Date"     ::date        ::set-date :date]
       [c/input-field       "Time"     ::time        ::set-time :time]
       [c/currency-selector "Currency" ::currency-id ::set-currency-id]
       [:pre (str form-data)]
       [c/primary-button    "Submit"   [::submit-clicked]]])]))

(defn init-form
  [{:keys [db]} _]
  (timbre/info "Init form")
  (let [default-date (js/Date.)]
    {:db (merge db {::rate (str default-rate)
                    ::date "2019-12-01"
                    ::time "00:00"})}))

(kf/reg-event-fx ::init-form init-form)

(kf/reg-controller
 ::form-controller
 {:params (constantly true)
  :start [::init-form]})
