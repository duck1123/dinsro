(ns dinsro.components.forms.registration-form
  (:require [ajax.core :as ajax]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-name "Bob")
(def default-email "bob@example.com")
(def default-password "hunter2")

(s/def ::name string?)
(rfu/reg-basic-sub ::name)
(rfu/reg-set-event ::name)

(s/def ::email            string?)
(rfu/reg-basic-sub ::email)
(rfu/reg-set-event ::email)


(s/def ::password         string?)
(rfu/reg-basic-sub ::password)
(rfu/reg-set-event ::password)

(s/def ::confirm-password string?)
(rfu/reg-basic-sub ::confirm-password)
(rfu/reg-set-event ::confirm-password)

(defn set-defaults
  [{:keys [db]} _]
  {:db (-> db
           (assoc ::name default-name)
           (assoc ::email default-email)
           (assoc ::password default-password)
           (assoc ::confirm-password default-password))})

(kf/reg-event-fx ::set-defaults set-defaults)

(defn create-form-data
  [[name email password] _]
  {:name name
   :email email
   :password password})

(rf/reg-sub
 ::form-data
 :<- [::name]
 :<- [::email]
 :<- [::password]
 create-form-data)

(defn register-succeeded
  [_ _]
  {})

(defn register-failed
  [_ _]
  {})

(defn submit-clicked
  [_ [form-data]]
  {:http-xhrio
   {:uri             "/api/v1/register"
    :method          :post
    :timeout         8000
    :format          (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})
    :params          form-data
    :on-success      [:register-succeeded]
    :on-failure      [:register-failed]}})

(kf/reg-event-fx :register-succeeded register-succeeded)
(kf/reg-event-fx :register-failed register-failed)
(kf/reg-event-fx ::submit-clicked submit-clicked)

(defn debug-box
  [data]
  [:pre (str data)])

(defn-spec registration-form (s/keys)
  []
  (let [form-data @(rf/subscribe [::form-data])]
    [:div.box
     [:form
      [debug-box form-data]
      [c/text-input     "Name"             ::name             ::set-name]
      [c/email-input    "Email"            ::email            ::set-email]
      [c/password-input "Password"         ::password         ::set-password]
      [c/password-input "Confirm Password" ::confirm-password ::set-confirm-password]
      [c/primary-button (tr [:submit]) [::submit-clicked form-data]]]]))
