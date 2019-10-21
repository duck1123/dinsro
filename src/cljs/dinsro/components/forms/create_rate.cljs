(ns dinsro.components.forms.create-rate
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub      ::rate        (fn-traced [db _] (get db ::rate "")))
(kf/reg-event-db ::change-rate (fn-traced [db [_ rate]] (assoc db ::rate rate)))

(rf/reg-sub
 ::form-data
 :<- [::rate]
 (fn-traced
   [rate]
   {:rate rate}))

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced
   [_ _]
   {:dispatech [::e.rates/do-submit #(rf/subscribe [::form-data])]}))

(defn create-rate-form
  []
  [:div
   [:p "Create rate"]
   [:form
    [c/text-input "Rate" ::rate ::change-rate]
    [c/primary-button "Submit" ::submit-clicked]]])
