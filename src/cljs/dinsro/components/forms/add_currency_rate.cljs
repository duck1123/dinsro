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
      #_(some-> @(rf/subscribe [::e.currencies/items]) first :db/id)
      ""))

(rf/reg-sub ::currency-id sub-currency-id)
;; (c/reg-field ::currency-id "")

(kf/reg-event-db ::change-currency-id (fn [db [value]] (assoc db ::currency-id value)))

(rf/reg-sub
 ::form-data
 :<- [::currency-id]
 :<- [::rate]
 (fn [[currency-id rate]]
   {:currency-id (int currency-id)
    :rate        rate}))

(defn submit-clicked
  [cofx event]
  (let [[data] event]
    {:dispatch [::e.rates/do-submit data]}))

(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn change-rate
  [db [value]]
  (assoc db ::rate (let [v (js/parseFloat value)] (if (js/isNaN v) 0 v))))

(kf/reg-event-db ::change-rate change-rate)

(defn add-currency-rate-form
  []
  (let [shown? @(rf/subscribe [::shown?])
        form-data @(rf/subscribe [::form-data])]
    [:div.box
     [:h3 "Add Currency rate"]
     [:button.button {:on-click #(rf/dispatch [::toggle])} (str "Toggle: " shown?)]
     (when shown?
       [:div
        #_[:pre (str form-data)]
        [c/number-input      "Rate"      ::rate        ::change-rate]
        [c/currency-selector "Currency"  ::currency-id ::change-currency-id]
        [c/primary-button    (l :submit) [::submit-clicked form-data]]])]))
