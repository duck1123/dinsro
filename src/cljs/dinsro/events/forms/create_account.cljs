(ns dinsro.events.forms.create-account
  (:require
   [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
   [dinsro.store :as st]))

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

(def form-data ::form-data)

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::s.e.f.create-account/name)
    (st/reg-set-event ::s.e.f.create-account/name)

    (st/reg-basic-sub ::s.e.f.create-account/currency-id)
    (st/reg-set-event ::s.e.f.create-account/currency-id)

    (st/reg-basic-sub ::s.e.f.create-account/user-id)
    (st/reg-set-event ::s.e.f.create-account/user-id)

    (st/reg-basic-sub ::shown?)
    (st/reg-set-event ::shown?)

    (st/reg-basic-sub ::s.e.f.create-account/initial-value)
    (st/reg-set-event ::s.e.f.create-account/initial-value)
    (st/reg-sub ::form-data form-data-sub))
  store)
