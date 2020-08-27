(ns dinsro.events.forms.add-user-category
  (:require
   [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
   [dinsro.store :as st]))

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
      (st/reg-basic-sub ::shown?)
      (st/reg-set-event ::shown?)
      (st/reg-sub ::form-data form-data-sub))
  store)
