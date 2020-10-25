(ns dinsro.events.forms.login
  (:require
   [clojure.spec.alpha]
   [dinsro.event-utils :as eu]
   [dinsro.spec.actions.login :as s.a.login]
   [dinsro.spec.events.forms.login :as s.e.f.login]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.login)

(eu/declare-form
 ns-sym
 ::s.a.login/create-params-valid
 [[:email    ::s.e.f.login/email    ""]
  [:password ::s.e.f.login/password ""]])

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
    (eu/register-form ns-sym)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::set-defaults set-defaults))
  store)
