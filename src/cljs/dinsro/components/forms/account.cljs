(ns dinsro.components.forms.account
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.accounts :as e.accounts]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::name          "")
(c/reg-field ::initial-value 0)
(c/reg-field ::form-shown?   true)
(c/reg-field ::currency-id   1)
(kf/reg-event-db ::change-currency-id   (fn [db [value]] (assoc db ::currency-id   (int value))))
(kf/reg-event-db ::change-name          (fn [db [value]] (assoc db ::name          value)))
(kf/reg-event-db ::change-initial-value (fn [db [value]] (assoc db ::initial-value value)))

(kf/reg-event-db ::toggle-form (fn-traced [db _] (update db ::form-shown? not)))

(rf/reg-sub
 ::account-data
 :<- [::name]
 :<- [::initial-value]
 :<- [::currency-id]
 (fn-traced [[name initial-value currency-id] _]
   {:name          name
    :currency-id   currency-id
    :initial-value initial-value}))

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced [_ _]
  {:dispatch [::e.accounts/do-submit @(rf/subscribe [::account-data])]}))

(defn new-account-form
  []
  [:<>
   [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"]
   [:div.section {:class (when-not @(rf/subscribe [::form-shown?]) "is-hidden")}
    [:pre (str @(rf/subscribe [::account-data]))]
    [:form.form
     [c/text-input        "Name"          ::name          ::change-name]
     [c/number-input      "Initial Value" ::initial-value ::change-initial-value]
     [c/currency-selector "Currency"      ::currency-id   ::change-currency-id]
     [c/primary-button    "Submit"        ::submit-clicked]]]])
