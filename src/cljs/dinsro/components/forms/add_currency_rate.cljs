(ns dinsro.components.forms.add-currency-rate
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(def l {:submit "Submit"})

(rf/reg-sub ::shown? (fn [db _] (get db ::shown?)))
(c/reg-field ::date        nil #_(.toISOString (js/Date.)))
(c/reg-field ::time        nil #_(.toISOString (js/Date.)))
(kf/reg-event-db ::change-time        (fn-traced [db [value]] (assoc db ::time value)))
(kf/reg-event-db ::change-date        (fn-traced [db [value]] (assoc db ::date value)))

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::shown? not)}))

(kf/reg-event-fx ::toggle toggle)

(c/reg-field ::rate        1)

(s/def ::currency-id string? #_(s/or :unselected string? :selected pos-int?))

(defn sub-currency-id
  [db _]
  (or (get db ::currency-id)
      (some-> @(rf/subscribe [::e.currencies/items]) first :db/id)
      ""))

(rf/reg-sub ::currency-id sub-currency-id)
;; (c/reg-field ::currency-id "")

(defn change-currency-id
  [db [value]]
  (assoc db ::currency-id value))

(defn change-rate
  [db [value]]
  (assoc db ::rate (let [v (js/parseFloat value)] (if (js/isNaN v) 0 v))))

(defn submit-clicked
  [cofx event]
  (let [[data] event]
    {:dispatch [::e.rates/do-submit data]}))

(rf/reg-sub
 ::form-data
 :<- [::currency-id]
 :<- [::rate]
 :<- [::date]
 :<- [::time]
 (fn [[currency-id rate date time]]
   {:currency-id (int currency-id)
    :rate        rate
    :date        (js/Date. (str date "T" time))}))

(kf/reg-event-db ::change-currency-id change-currency-id)
(kf/reg-event-db ::change-rate change-rate)
(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn add-currency-rate-form
  [currency-id]
  (let [shown? @(rf/subscribe [::shown?])
        form-data (assoc @(rf/subscribe [::form-data]) :currency-id currency-id)]
    [:<>
     [:div
      #_[:span (str shown?)]
      [:a {:style {:margin-left "5px"}
           :on-click #(rf/dispatch [::toggle])}
       (if shown?
         [:span.icon>i.fas.fa-chevron-down]
         [:span.icon>i.fas.fa-chevron-right])]]
     (when shown?
       [:<>
        #_[:p "Currency Id: " currency-id]
        [c/number-input "Rate" ::rate ::change-rate]
        [c/input-field "Date" ::date ::change-date :date]
        [c/input-field "Time" ::time ::change-time :time]
        #_[c/currency-selector "Currency"  ::currency-id ::change-currency-id]
        [:pre (str form-data)]
        [c/primary-button (l :submit) [::submit-clicked form-data]]])]))
