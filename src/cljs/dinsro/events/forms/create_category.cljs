(ns dinsro.events.forms.create-category
  (:require
   [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(defn form-data-sub
  [{:keys [::s.e.f.create-category/name
           ::s.e.f.create-category/user-id]}
   _]
  {:name          name
   :user-id       (int user-id)})

(def form-data ::form-data)

(defn init-handlers!
  [store]
  (doto store
      (st/reg-basic-sub ::s.e.f.create-category/name)
      (st/reg-set-event ::s.e.f.create-category/name)
      (st/reg-basic-sub ::s.e.f.create-category/user-id)
      (st/reg-set-event ::s.e.f.create-category/user-id)
      (st/reg-basic-sub ::shown?)
      (st/reg-set-event ::shown?)
      (st/reg-sub ::form-data form-data-sub))
  store)
