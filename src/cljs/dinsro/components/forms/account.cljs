(ns dinsro.components.forms.account
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.accounts :as e.accounts]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::name          "")
(c/reg-field ::initial-value 0)
(c/reg-field ::form-shown?   true)
(kf/reg-event-db ::change-name          (fn [db [value]] (assoc db ::name          value)))
(kf/reg-event-db ::change-initial-value (fn [db [value]] (assoc db ::initial-value value)))

(rf/reg-sub
 ::account-data
 :<- [::name]
 (fn-traced [name _]
   {:name name}))

(kf/reg-event-db
 ::toggle-form
 (fn-traced [db _]
   (update db ::form-shown? not)))

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced [_ _]
  {:dispatch [::e.accounts/do-submit @(rf/subscribe [::account-data])]}))

(defn new-account-form
  []
  (let [form-shown? @(rf/subscribe [::form-shown?])]
    [:div.section
     [:a.button {:on-click #(rf/dispatch [::toggle-form])} "Toggle"]
     [:div.section {:class (when-not form-shown? "is-hidden")}
      [:p "New Account Form"]
      [:pre (str @(rf/subscribe [::account-data]))]
      [:form.form
       [c/text-input     "Name"          ::name          ::change-name]
       [c/text-input     "Initial Value" ::initial-value ::initial-value]
       [c/currency-selector]
       [c/primary-button "Submit"        ::submit-clicked]]]]))
