(ns dinsro.events.forms.create-currency
  (:require
   [clojure.spec.alpha]
   [dinsro.event-utils :as eu]
   [dinsro.spec.events.forms.create-currency :as s.e.f.create-currency]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.create-currency)

(defn form-data-sub
  [{:keys [::s.e.f.create-currency/name]} _]
  {:name name})

(defn set-defaults
  [{:keys [db]} _]
  {:db (merge db {::s.e.f.create-currency/name s.e.f.create-currency/default-name})})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-form ns-sym)

    (st/reg-basic-sub ::s.e.f.create-currency/name)
    (st/reg-set-event ::s.e.f.create-currency/name)

    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::set-defaults set-defaults))
  store)
