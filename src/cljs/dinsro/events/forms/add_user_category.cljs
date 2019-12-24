(ns dinsro.events.forms.add-user-category
  (:require [dinsro.spec.events.forms.add-user-category :as s.e.f.add-user-category]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::s.e.f.add-user-category/shown?)
(rfu/reg-set-event ::s.e.f.add-user-category/shown?)

(rfu/reg-basic-sub ::s.e.f.add-user-category/name)
(rfu/reg-set-event ::s.e.f.add-user-category/name)

(rfu/reg-basic-sub ::s.e.f.add-user-category/user-id)
(rfu/reg-set-event ::s.e.f.add-user-category/user-id)

(defn form-data-sub
  [[name user-id] _]
  {:name          name
   :user-id       (int user-id)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.add-user-category/name]
 :<- [::s.e.f.add-user-category/user-id]
 form-data-sub)
(def form-data ::form-data)
