(ns dinsro.events.forms.registration
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec.events.forms.registration :as s.e.f.registration]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::s.e.f.registration/name)
(rfu/reg-set-event ::s.e.f.registration/name)

(rfu/reg-basic-sub ::s.e.f.registration/email)
(rfu/reg-set-event ::s.e.f.registration/email)

(rfu/reg-basic-sub ::s.e.f.registration/password)
(rfu/reg-set-event ::s.e.f.registration/password)

(rfu/reg-basic-sub ::s.e.f.registration/confirm-password)
(rfu/reg-set-event ::s.e.f.registration/confirm-password)

(rfu/reg-basic-sub ::s.e.f.registration/error-message)
(rfu/reg-set-event ::s.e.f.registration/error-message)

(defn set-defaults
  [{:keys [db]} _]
  {:db (-> db
           (assoc ::s.e.f.registration/name s.e.f.registration/default-name)
           (assoc ::s.e.f.registration/email s.e.f.registration/default-email)
           (assoc ::s.e.f.registration/password s.e.f.registration/default-password)
           (assoc ::s.e.f.registration/confirm-password s.e.f.registration/default-password)
           (assoc ::s.e.f.registration/error-message s.e.f.registration/default-error-message))})

(kf/reg-event-fx ::set-defaults set-defaults)

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

(rf/reg-sub ::form-data form-data-sub)
(def form-data ::form-data)

(s/def ::form-data
  (s/keys :req-un [::s.e.f.registration/name
                   ::s.e.f.registration/email
                   ::s.e.f.registration/password
                   ::s.e.f.registration/confirm-password]))
