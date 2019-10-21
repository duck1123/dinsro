(ns dinsro.components.forms.create-rate
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::rate "")
(kf/reg-event-db ::change-rate (fn-traced [db [_ rate]] (assoc db ::rate rate)))
(rf/reg-sub      ::form-shown? (fn-traced [db _] (get db ::form-shown? false)))

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

(kf/reg-event-db
 ::toggle-form
 (fn-traced [db _]
   (assoc db ::form-shown? (not (get db ::form-shown?)))))

(defn create-rate-form
  []
  (let [form-shown? @(rf/subscribe [::form-shown?])]
    [:div
     [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"]
     [:div.section {:class (when-not form-shown? "is-hidden")
                    :style {:border "1px black solid"}}
      [:p "Create rate"]
      [:form
       [c/text-input "Rate" ::rate ::change-rate]
       [c/currency-selector]
       [c/primary-button "Submit" ::submit-clicked]]]]))
