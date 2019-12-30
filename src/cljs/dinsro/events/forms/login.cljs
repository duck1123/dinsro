(ns dinsro.events.forms.login
  (:require [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(def default-email "bob@example.com")
(def default-password "hunter2")

(rfu/reg-basic-sub ::email)
(rfu/reg-set-event ::email)

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
