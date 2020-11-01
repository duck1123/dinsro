(ns dinsro.events.forms.create-currency
  (:require
   [clojure.spec.alpha]
   [dinsro.events.utils :as eu]
   [dinsro.specs.actions.currencies :as s.a.currencies]
   [dinsro.specs.events.forms.create-currency :as s.e.f.create-currency]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.create-currency)

(eu/declare-form
 ns-sym
 ::s.a.currencies/create-params-valid
 [[:name ::s.e.f.create-currency/name ""]])

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
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::set-defaults set-defaults))
  store)
