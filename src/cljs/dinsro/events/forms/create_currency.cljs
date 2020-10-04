(ns dinsro.events.forms.create-currency
  (:require
   [dinsro.spec.events.forms.create-currency :as s.e.f.create-currency]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(defn form-data-sub
  [{:keys [::s.e.f.create-currency/name]} _]
  {:name name})

(def form-data ::form-data)

(defn set-defaults
  [{:keys [db]} _]
  {:db (merge db {::s.e.f.create-currency/name s.e.f.create-currency/default-name})})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::s.e.f.create-currency/name)
    (st/reg-set-event ::s.e.f.create-currency/name)

    (st/reg-basic-sub ::shown?)
    (st/reg-set-event ::shown?)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::set-defaults set-defaults))
  store)
