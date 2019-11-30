(ns dinsro.components.forms.create-currency
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.currencies :as e.currencies]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::name "")

(defn change-name
  [db [value]]
  (assoc db ::name value))

(kf/reg-event-db ::change-name change-name)

(rf/reg-sub
 ::form-data
 :<- [::name]
 (fn-traced [name]
   {:name name}))

(defn submit-clicked
  [_ _]
  (let [form-data @(rf/subscribe [::form-data])]
    {:dispatch [::e.currencies/do-submit form-data]}))

(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn create-currency
  []
  [:div.box
   #_[:p "Create Currency"]
   #_[:pre (str @(rf/subscribe [::form-data]))]
   [:form
    [c/text-input        "Name"     ::name        ::change-name]
    [c/primary-button "Submit" [::submit-clicked]]]])
