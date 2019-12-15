(ns dinsro.components.forms.add-account-transactions
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

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(s/def ::currency-id string?)
(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(s/def ::date string?)
(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(s/def ::value string?)
(rfu/reg-basic-sub ::value)
(rfu/reg-set-event ::value)

(defn toggle
  [cofx event]
  (let [{:keys [db]} cofx]
    {:db (update db ::shown? not)}))

(kf/reg-event-fx ::toggle toggle)

(defn toggle-button
  []
  (let [shown? @(rf/subscribe [::shown?])]
    [:a {:on-click #(rf/dispatch [::toggle])}
     (if shown?
       [:span.icon>i.fas.fa-chevron-down]
       [:span.icon>i.fas.fa-chevron-right])]))

(defn create-form-data
  [[value currency-id date]]
  {:value value
   :currency-id (int currency-id)
   :date        (js/Date. date)})

(rf/reg-sub
 ::form-data
 :<- [::value]
 :<- [::currency-id]
 :<- [::date]
 create-form-data)

(defn form
  []
  [:<>
   (let [form-data @(rf/subscribe [::form-data])]
     (when @(rf/subscribe [::shown?])
       [:div
        [c/close-button ::set-shown?]
        [c.debug/debug-box form-data]
        [:div.field>div.control
         [c/number-input (tr [:value]) ::value ::set-value]]
        [:div.field>div.control
         [c/currency-selector (tr [:currency]) ::currency-id ::set-currency-id]]
        [:div.field>div.control
         [c.datepicker/datepicker
          {:on-select #(rf/dispatch [::set-date (timbre/spy :info %)])}]]
        [:div.field>div.control
         [c/primary-button (tr [:submit]) [::submit-clicked]]]]))])
