(ns dinsro.components.forms.create-rate
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::rate        0)
(c/reg-field ::currency-id 2)
(c/reg-field ::date        (.toISOString (js/Date.)))
(c/reg-field ::time        (.toISOString (js/Date.)))
(c/reg-field ::form-shown? false)
#_(rf/reg-sub      ::form-shown?          (fn-traced [db _] (get db ::form-shown? false)))

(kf/reg-event-db ::change-currency-id (fn-traced [db [value]] (assoc db ::currency-id (int value))))
(kf/reg-event-db ::change-date        (fn-traced [db [value]] (assoc db ::date value)))
(kf/reg-event-db ::change-time        (fn-traced [db [value]] (assoc db ::time value)))
(kf/reg-event-db
 ::change-rate
 (fn-traced [db [value]]
   (assoc db ::rate (let [v (js/parseFloat value)] (if (js/isNaN v) 0 v)))))

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

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced
   [_ _]
   {:dispatch [::e.rates/do-submit #(rf/subscribe [::form-data])]}))

(kf/reg-event-db
 ::toggle-form
 (fn-traced [db _]
   (update db ::form-shown? not)))

(defn create-rate-form
  []
  [:<>
   [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"]
   [:div.section {:class (when-not @(rf/subscribe [::form-shown?]) "is-hidden")
                  :style {:border "1px black solid"}}
    [:pre (str @(rf/subscribe [::form-data]))]
    [:form
     [c/number-input        "Rate"     ::rate        ::change-rate]
     [c/input-field "Date" ::date ::change-date :date]
     [c/input-field "Time" ::time ::change-time :time]
     [c/currency-selector "Currency" ::currency-id ::change-currency-id]
     [c/primary-button    "Submit"   ::submit-clicked]]]])
