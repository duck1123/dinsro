(ns dinsro.events.forms.registration-form
  (:require [dinsro.spec.events.forms.registration :as s.e.f.registration]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.registration/name)
(rfu/reg-set-event ::s.e.f.registration/name)

(rfu/reg-basic-sub ::s.e.f.registration/email)
(rfu/reg-set-event ::s.e.f.registration/email)

(rfu/reg-basic-sub ::s.e.f.registration/password)
(rfu/reg-set-event ::s.e.f.registration/password)

(rfu/reg-basic-sub ::s.e.f.registration/confirm-password)
(rfu/reg-set-event ::s.e.f.registration/confirm-password)

(defn set-defaults
  [{:keys [db]} _]
  {:db (-> db
           (assoc ::s.e.f.registration/name s.e.f.registration/default-name)
           (assoc ::s.e.f.registration/email s.e.f.registration/default-email)
           (assoc ::s.e.f.registration/password s.e.f.registration/default-password)
           (assoc ::s.e.f.registration/confirm-password s.e.f.registration/default-password))})

(kf/reg-event-fx ::set-defaults set-defaults)

(defn form-data-sub
  [[name email password] _]
  {:name name
   :email email
   :password password})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.registration/name]
 :<- [::s.e.f.registration/email]
 :<- [::s.e.f.registration/password]
 form-data-sub)
