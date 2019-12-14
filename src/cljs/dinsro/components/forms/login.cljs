(ns dinsro.components.forms.login
  (:require [ajax.core :as ajax]
            [cemerick.url :as url]
            [clojure.string :as string]
            [clojure.spec.alpha :as s]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.authentication :as e.authentication]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-email "bob@example.com")
(def default-password "hunter2")

(s/def ::email string?)
(rfu/reg-basic-sub ::email)
(rfu/reg-set-event ::email)

(s/def ::password string?)
(rfu/reg-basic-sub ::password)
(rfu/reg-set-event ::password)

(defn create-form-data
  [[email password] _]
  {:email email
   :password password})

(rf/reg-sub
 ::form-data
 :<- [::email]
 :<- [::password]
 create-form-data)

(defn set-defaults
  [{:keys [db]} _]
  {:db (-> db
           (assoc ::email default-email)
           (assoc ::password default-password))})

(kf/reg-event-fx ::set-defaults set-defaults)

(defn debug-box [data] [:pre (str data)])

(defn login-form
  [return-to]
  (let [form-data @(rf/subscribe [::form-data])]
    [:form.is-centered
     [c.debug/debug-box form-data]
     [c/email-input (tr [:email]) ::email ::set-email]
     [c/password-input (tr [:password]) ::password ::set-password]
     [c/primary-button (tr [:login]) [::e.authentication/do-authenticate form-data return-to]]]))
