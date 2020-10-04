(ns dinsro.events.forms.registration
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu]
   [dinsro.spec.events.forms.registration :as s.e.f.registration]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.registration)

(defn set-defaults
  [{:keys [db]} _]
  {:db (-> db
           (assoc ::s.e.f.registration/name s.e.f.registration/default-name)
           (assoc ::s.e.f.registration/email s.e.f.registration/default-email)
           (assoc ::s.e.f.registration/password s.e.f.registration/default-password)
           (assoc ::s.e.f.registration/confirm-password s.e.f.registration/default-password)
           (assoc ::s.e.f.registration/error-message s.e.f.registration/default-error-message))})

(s/def ::form-data
  (s/keys :req-un [::s.e.f.registration/name
                   ::s.e.f.registration/email
                   ::s.e.f.registration/password
                   ::s.e.f.registration/confirm-password]))

(defn form-data-sub
  [{:keys [::s.e.f.registration/email
           ::s.e.f.registration/name
           ::s.e.f.registration/password]}
   _]
  {:name name
   :email email
   :password password})

(s/def ::form-data-params (s/cat :name s.e.f.registration/name
                                 :email s.e.f.registration/email
                                 :password s.e.f.registration/password))

(s/fdef form-data-sub
  :args (s/cat ))

(s/def ::form-data
  (s/keys :req-un [::s.e.f.registration/name
                   ::s.e.f.registration/email
                   ::s.e.f.registration/password
                   ::s.e.f.registration/confirm-password]))

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::s.e.f.registration/name)
    (st/reg-set-event ::s.e.f.registration/name)
    (st/reg-basic-sub ::s.e.f.registration/email)
    (st/reg-set-event ::s.e.f.registration/email)
    (st/reg-basic-sub ::s.e.f.registration/password)
    (st/reg-set-event ::s.e.f.registration/password)
    (st/reg-basic-sub ::s.e.f.registration/confirm-password)
    (st/reg-set-event ::s.e.f.registration/confirm-password)
    (st/reg-basic-sub ::s.e.f.registration/error-message)
    (st/reg-set-event ::s.e.f.registration/error-message)
    (st/reg-event-fx ::set-defaults set-defaults)
    (st/reg-sub ::form-data form-data-sub))
  store)
