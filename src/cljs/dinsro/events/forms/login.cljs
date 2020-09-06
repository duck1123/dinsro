(ns dinsro.events.forms.login
  (:require
   [dinsro.spec.events.forms.login :as s.e.f.login]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.login/email)
(rfu/reg-set-event ::s.e.f.login/email)

(rfu/reg-basic-sub ::s.e.f.login/password)
(rfu/reg-set-event ::s.e.f.login/password)

(defn form-data-sub
  [{:keys [::s.e.f.login/email
           ::s.e.f.login/password]}
    _]
  {:email email
   :password password})

(rf/reg-sub ::form-data form-data-sub)
(def form-data ::form-data)

(defn set-defaults
  [{:keys [db]} _]
  {:db (-> db
           (assoc ::s.e.f.login/email "")
           (assoc ::s.e.f.login/password ""))})

(kf/reg-event-fx ::set-defaults set-defaults)
