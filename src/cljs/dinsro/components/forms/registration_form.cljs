(ns dinsro.components.forms.registration-form
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(c/reg-field ::name             "")
(c/reg-field ::email            "")
(c/reg-field ::password         "hunter2")
(c/reg-field ::confirm-password "hunter2")

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

(defn-spec submit-clicked nil? #_(s/keys)
  [{:keys [db]} (s/keys) _ any?]
  (let [email @(rf/subscribe [::email])
        name @(rf/subscribe [::name])
        confirm-password @(rf/subscribe [::confirm-password])
        password @(rf/subscribe [::password])
        params {:dinsro.model.user/name name
                :dinsro.model.user/email email
                :dinsro.model.user/password password
                :dinsro.model.user/confirm-password confirm-password}]
    {:db db
     :http-xhrio
     {:uri             "/api/v1/register"
      :method          :post
      :timeout         8000
      :format          (ajax/json-request-format)
      :response-format (ajax/json-response-format {:keywords? true})
      :params          params
      :on-success      [:register-succeeded]
      :on-failure      [:register-failed]}}))

(kf/reg-event-fx ::submit-clicked submit-clicked)

(kf/reg-event-db
 ::load-register-page
 (fn-traced
  [db _]
  (-> db
      (assoc ::name "Bob")
      (assoc ::email "bob@example.com")
      (assoc ::password "hunter2")
      (assoc ::confirm-password "hunter2"))))

(kf/reg-controller
 ::registration-form
 {:params (constantly true)
  :start [::load-register-page]})

(defn-spec registration-form (s/keys)
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
