(ns dinsro.components.forms.create-rate
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.rates :as e.rates]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::rate          0)
(c/reg-field ::currency-id   2)
(c/reg-field ::form-shown?   false)
#_(rf/reg-sub      ::form-shown?          (fn-traced [db _] (get db ::form-shown? false)))

(kf/reg-event-db ::change-currency-id   (fn-traced [db [value]] (assoc db ::currency-id (int value))))
(kf/reg-event-db
 ::change-rate
 (fn-traced [db [value]]
   (assoc db ::rate (let [v (js/parseFloat value)] (if (js/isNaN v) 0 v)))))

(rf/reg-sub
 ::form-data
 :<- [::currency-id]
 :<- [::rate]
 (fn-traced [[currency-id rate]]
   {:currency-id currency-id
    :rate        rate}))

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
  [:<>
   [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"]
   [:div.section {:class (when-not @(rf/subscribe [::form-shown?]) "is-hidden")
                  :style {:border "1px black solid"}}
    [:pre (str @(rf/subscribe [::form-data]))]
    [:form
     [c/number-input        "Rate"     ::rate        ::change-rate]
     [c/currency-selector "Currency" ::currency-id ::change-currency-id]
     [c/primary-button    "Submit"   ::submit-clicked]]]])
