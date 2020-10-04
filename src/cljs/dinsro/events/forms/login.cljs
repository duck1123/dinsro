(ns dinsro.events.forms.login
  (:require
   [clojure.spec.alpha]
   [dinsro.event-utils :as eu]
   [dinsro.spec.events.forms.login :as s.e.f.login]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.login)

(defn form-data-sub
  [{:keys [::s.e.f.login/email
           ::s.e.f.login/password]}
   _]
  {:email email
   :password password})

(defn set-defaults
  [{:keys [db]} _]
  {:db (-> db
           (assoc ::s.e.f.login/email "")
           (assoc ::s.e.f.login/password ""))})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::s.e.f.login/email)
    (st/reg-set-event ::s.e.f.login/email)
    (st/reg-basic-sub ::s.e.f.login/password)
    (st/reg-set-event ::s.e.f.login/password)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::set-defaults set-defaults))
  store)
