(ns dinsro.components.forms.account
  (:require [ajax.core :as ajax]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(rf/reg-sub ::do-submit-loading ::do-submit-loading)
(rf/reg-sub ::name              ::name)

(rf/reg-event-db ::change-name (fn [db [_ name]] (assoc db ::name name)))

(rf/reg-sub
 ::account-data
 :<- [::name]
 (fn [name _]
   {:name name}))

(kf/reg-event-fx
 ::do-submit-succeeded
 (fn-traced
  [_ data]
  (timbre/info "Submit success" data)))

(kf/reg-event-fx
 ::do-submit-failed
 (fn-traced
  [_ [_ response]]
  (timbre/info "Submit failed" response)))

(kf/reg-event-fx
 ::do-submit
 (fn-traced [_ [_ data]]
   {:http-xhrio
    {:method :post
     :uri "/api/v1/accounts"
     :params data
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success [::do-submit-succeeded]
     :on-failure [::do-submit-failed]}}))

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced
  [{:keys [db]} _]
  {:db (assoc db ::do-submit-loading true)
   :dispatch [::do-submit @(rf/subscribe [::account-data])]}))

(defn new-account-form
  []
  [:div
   [:p "New Account Form"]
   [:form
    [c/text-input "Name" ::name ::change-name]
    [c/primary-button "Submit" ::submit-clicked]]])
