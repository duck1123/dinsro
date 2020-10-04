(ns dinsro.events.forms.create-account
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.event-utils :as eu]
   [dinsro.spec.actions.accounts :as s.a.accounts]
   [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.create-account)

(eu/declare-form
 ns-sym
 [::s.e.f.create-account/currency-id
  ::s.e.f.create-account/initial-value
  ::s.e.f.create-account/name
  ::s.e.f.create-account/user-id])

;; (s/def ::form-data (s/keys))
(s/def ::form-data-db (s/keys :req [::s.e.f.create-account/currency-id
                                    ::s.e.f.create-account/initial-value
                                    ::s.e.f.create-account/name
                                    ::s.e.f.create-account/user-id]))
(s/def ::form-data-event (s/cat :kw keyword?))
(s/def ::form-data-request (s/cat :db ::form-data-db
                                  :event ::form-data-event))
(s/def ::form-data-response ::s.a.accounts/create-params-valid)

(defn form-data-sub
  [{:keys [::s.e.f.create-account/currency-id
           ::s.e.f.create-account/initial-value
           ::s.e.f.create-account/name
           ::s.e.f.create-account/user-id]}
    _]
  {:name          name
   :currency-id   (int currency-id)
   :user-id       (int user-id)
   :initial-value (.parseFloat js/Number initial-value)})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-form ns-sym)

    (st/reg-basic-sub ::s.e.f.create-account/name)
    (st/reg-set-event ::s.e.f.create-account/name)

    (st/reg-basic-sub ::s.e.f.create-account/currency-id)
    (st/reg-set-event ::s.e.f.create-account/currency-id)

    (st/reg-basic-sub ::s.e.f.create-account/user-id)
    (st/reg-set-event ::s.e.f.create-account/user-id)

    (st/reg-basic-sub ::s.e.f.create-account/initial-value)
    (st/reg-set-event ::s.e.f.create-account/initial-value)
    (st/reg-sub ::form-data form-data-sub))
  store)
