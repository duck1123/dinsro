(ns dinsro.components.forms.create-transaction
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.datepicker :as c.datepicker]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::currency-id string?)
(rfu/reg-basic-sub ::currency-id)
(rfu/reg-set-event ::currency-id)

(s/def ::date string?)
(rfu/reg-basic-sub ::date)
(rfu/reg-set-event ::date)

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(s/def ::value string?)
(rfu/reg-basic-sub ::value)
(rfu/reg-set-event ::value)

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

(defn toggle-form
  [db _]
  (update db ::shown? not))

(kf/reg-event-db ::toggle-form toggle-form)

(defn toggle-button
  []
  [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"])

(defn close-button
  []
  [:a.delete.is-pulled-right
   {:on-click #(rf/dispatch [::set-shown? false])}])

(defn create-transaction-form
  []
  (let [form-data @(rf/subscribe [::form-data])]
    [:div
     [close-button]
     [:p "Create transaction form"]
     [:div.field>div.control
      [c/number-input      "Value"     ::value        ::set-value]]
     [:div.field>div.control
      [c/currency-selector "Currency" ::currency-id ::set-currency-id]]
     [:div.field>div.control
      [c/primary-button    "Submit"   [::submit-clicked]]]]))
