(ns dinsro.events.forms.create-rate-source
  (:require
   [clojure.spec.alpha]
   [dinsro.events.utils :as eu]
   [dinsro.spec.actions.rate-sources :as s.a.rate-sources]
   [dinsro.spec.events.forms.create-rate-source :as s.e.f.create-rate-source]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.create-rate-source)

(def default-name "Default Source")

(eu/declare-form
 ns-sym
 ::s.a.rate-sources/create-params-valid
 [[:currency-id ::s.e.f.create-rate-source/currency-id 0]
  [:name        ::s.e.f.create-rate-source/name        default-name]
  [:url         ::s.e.f.create-rate-source/url         ""]])

(defn form-data-sub
  [{:keys [::s.e.f.create-rate-source/currency-id
           ::s.e.f.create-rate-source/name
           ::s.e.f.create-rate-source/url]}
    _]
  {:name name
   :url url
   :currency-id (int currency-id)})

(defn init-form
  [{:keys [db]} _]
  {:db
   (merge
    db
    {::s.e.f.create-rate-source/name        (str s.e.f.create-rate-source/default-name)
     ::s.e.f.create-rate-source/url         s.e.f.create-rate-source/default-url
     ::s.e.f.create-rate-source/currency-id (str s.e.f.create-rate-source/default-currency-id)})})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-form ns-sym)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::init-form init-form))
  store)
