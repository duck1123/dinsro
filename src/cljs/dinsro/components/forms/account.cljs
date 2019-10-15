(ns dinsro.components.forms.account
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.events.accounts :as e.accounts]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::name              ::name)

(rf/reg-event-db ::change-name (fn [db [_ name]] (assoc db ::name name)))

(rf/reg-sub
 ::account-data
 :<- [::name]
 (fn [name _]
   {:name name}))

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced
  [{:keys [db]} _]
  {:db (assoc db ::e.accounts/do-submit-loading true)
   :dispatch [::do-submit @(rf/subscribe [::account-data])]}))

(defn new-account-form
  []
  [:div
   [:p "New Account Form"]
   [:form
    [c/text-input "Name" ::name ::change-name]
    [c/primary-button "Submit" ::submit-clicked]]])
