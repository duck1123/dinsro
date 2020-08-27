(ns dinsro.events.forms.create-rate-source
  (:require
   [dinsro.spec.events.forms.create-rate-source :as s.e.f.create-rate-source]
   [dinsro.store :as st]))

(def default-name "Default Source")

(defn form-data-sub
  [{:keys [::s.e.f.create-rate-source/currency-id
           ::s.e.f.create-rate-source/name
           ::s.e.f.create-rate-source/url]}
    _]
  {:name name
   :url url
   :currency-id (int currency-id)})

(def form-data ::form-data)

(defn init-form
  [{:keys [db]} _]
  {:db (merge db {
                  ::s.e.f.create-rate-source/name (str s.e.f.create-rate-source/default-name)
                  ::s.e.f.create-rate-source/url s.e.f.create-rate-source/default-url
                  ::s.e.f.create-rate-source/currency-id (str s.e.f.create-rate-source/default-currency-id)
                  })})


(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::s.e.f.create-rate-source/name)
    (st/reg-set-event ::s.e.f.create-rate-source/name)
    (st/reg-basic-sub ::s.e.f.create-rate-source/url)
    (st/reg-set-event ::s.e.f.create-rate-source/url)
    (st/reg-basic-sub ::s.e.f.create-rate-source/currency-id)
    (st/reg-set-event ::s.e.f.create-rate-source/currency-id)
    (st/reg-basic-sub ::shown?)
    (st/reg-set-event ::shown?)
    (st/reg-sub ::form-data form-data-sub)
    (st/reg-event-fx ::init-form init-form))
  store)
