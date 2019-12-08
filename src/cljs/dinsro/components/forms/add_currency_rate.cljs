(ns dinsro.components.forms.add-currency-rate
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rf-utils]
            [taoensso.timbre :as timbre]))

(rf-utils/reg-basic-sub ::shown?)

(def default-rate 1)

(s/def ::date string?)
(rf-utils/reg-basic-sub ::date)
(rf-utils/reg-set-event ::date)

(s/def ::time string?)
(rf-utils/reg-basic-sub ::time)
(rf-utils/reg-set-event ::time)

(s/def ::rate string?)
(rf-utils/reg-basic-sub ::rate)
(rf-utils/reg-set-event ::rate)

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::shown? not)}))

(kf/reg-event-fx ::toggle toggle)

;; FIXME: id for string or int
;; (s/or :unselected string? :selected pos-int?)
(s/def ::currency-id string?)

(defn sub-currency-id
  [db _]
  (or (get db ::currency-id)
      ;; FIXME: Don't do this. do in init
      (some-> @(rf/subscribe [::e.currencies/items]) first :db/id)
      ""))

(rf/reg-sub ::currency-id sub-currency-id)
(rf-utils/reg-set-event ::currency-id)
(rf-utils/reg-set-event ::rate)

(defn submit-clicked
  [cofx event]
  (let [[data] event]
    {:dispatch [::e.rates/do-submit data]}))

(s/def ::add-currency-rate-form
  (s/keys :req-un [::s.rates/date ::s.rates/rate ::s.rates/currency-id]))

(defn create-form-data
  [[currency-id rate date time]]
  {:currency-id (int currency-id)
   :rate        rate
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

(defn debug-box
  [form-data]
  [:pre (str form-data)])

(defn add-currency-rate-form
  [currency-id]
  (let [shown? @(rf/subscribe [::shown?])]
    [:<>
     [toggle-button]
     (when shown?
       (let [form-data (assoc @(rf/subscribe [::form-data]) :currency-id currency-id)]
         [:<>
          [c/number-input "Rate" ::rate ::change-rate]
          [c/input-field "Date" ::date ::change-date :date]
          [c/input-field "Time" ::time ::change-time :time]
          [:p "Currency Id: " currency-id]
          #_[c/currency-selector "Currency"  ::currency-id ::change-currency-id]
          [debug-box form-data]
          [c/primary-button (tr [:submit]) [::submit-clicked form-data]]]))]))
