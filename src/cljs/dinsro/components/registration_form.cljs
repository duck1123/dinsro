(ns dinsro.components.registration-form
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::name             "")
(c/reg-field ::email            "")
(c/reg-field ::password         "")
(c/reg-field ::confirm-password "")

(s/def ::name             string?)
(s/def ::email            string?)
(s/def ::password         string?)
(s/def ::confirm-password string?)

(kf/reg-event-db
 ::change-email
 (fn-traced [db [value]]
   (assoc db ::email value)))

(kf/reg-event-db
 ::change-name
 (fn-traced [db [value]]
   (assoc db ::name value)))

(kf/reg-event-db
 ::change-password
 (fn-traced [db [value]]
   (assoc db ::password value)))

(kf/reg-event-db
 ::change-confirm-password
 (fn-traced [db [value]]
   (assoc db ::confirm-password value)))

(kf/reg-event-fx
 :register-succeeded
 (fn-traced
  [_ _]
  nil))

(kf/reg-event-fx
 :register-failed
 (fn-traced
  [_ _]
  nil))

(kf/reg-event-fx
 ::submit-clicked
 (fn-traced
  [{:keys [db]} event]
  (let [email @(rf/subscribe [::email])
        name @(rf/subscribe [::name])
        confirm-password @(rf/subscribe [::confirm-password])
        password @(rf/subscribe [::password])
        params {:name name
                :email email
                :password password
                :confirm-password confirm-password}]
    {:db db
     :http-xhrio
     {:uri             "/api/v1/register"
      :method          :post
      :timeout         8000
      :format          (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})
      :params          params
      :on-success      [:register-succeeded]
      :on-failure      [:register-failed]}})))

(kf/reg-event-db
 ::load-register-page
 (fn-traced
  [db _]
  (-> db
      (assoc ::name "Bob")
      (assoc ::email "bob@example.com")
      (assoc ::password "")
      (assoc ::confirm-password ""))))

(kf/reg-controller
 ::registration-form
 {:params (constantly true)
  :start [::load-register-page]})

(defn registration-form
  []
  [:form {:style {:border "1px solid red"}}
   [c/text-input     "Name"             ::name             ::change-name]
   [c/email-input    "Email"            ::email            ::change-email]
   [c/password-input "Password"         ::password         ::change-password]
   [c/password-input "Confirm Password" ::confirm-password ::change-confirm-password]
   [:div.field
    [:div.control
     [:a.button.is-primary
      {:on-click #(rf/dispatch [::submit-clicked])}
      "Submit"]]]])
