(ns dinsro.events.forms.login
  (:require [dinsro.spec.events.forms.registration :as s.e.f.registration]
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
  [[email password] _]
  {:email email
   :password password})

(rf/reg-sub
 ::s.e.f.login/form-data
 :<- [::s.e.f.login/email]
 :<- [::s.e.f.login/password]
 form-data-sub)

(defn set-defaults
  [{:keys [db]} _]
  {:db (-> db
           (assoc ::s.e.f.login/email s.e.f.registration/default-email)
           (assoc ::s.e.f.login/password s.e.f.registration/default-password))})

(kf/reg-event-fx ::s.e.f.login/set-defaults set-defaults)
