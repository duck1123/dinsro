(ns dinsro.events.forms.create-account
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.utils :as eu]
   [dinsro.specs.actions.accounts :as s.a.accounts]
   [dinsro.specs.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.create-account)

(eu/declare-form
 ns-sym
 ::s.a.accounts/create-params-valid
 [[:currency-id   ::s.e.f.create-account/currency-id   0]
  [:initial-value ::s.e.f.create-account/initial-value 0]
  [:name          ::s.e.f.create-account/name          ""]
  [:user-id       ::s.e.f.create-account/user-id       0]])

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
    (st/reg-sub ::form-data form-data-sub))
  store)
