(ns dinsro.events.forms.create-account
  (:require [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.create-account/name)
(rfu/reg-set-event ::s.e.f.create-account/name)

(rfu/reg-basic-sub ::s.e.f.create-account/currency-id)
(rfu/reg-set-event ::s.e.f.create-account/currency-id)

(rfu/reg-basic-sub ::s.e.f.create-account/user-id)
(rfu/reg-set-event ::s.e.f.create-account/user-id)

(rfu/reg-basic-sub ::s.e.f.create-account/shown?)
(rfu/reg-set-event ::s.e.f.create-account/shown?)

(rfu/reg-basic-sub ::s.e.f.create-account/initial-value)
(rfu/reg-set-event ::s.e.f.create-account/initial-value)

(defn form-data-sub
  [[name initial-value currency-id user-id] _]
  {:name          name
   :currency-id   (int currency-id)
   :user-id       (int user-id)
   :initial-value (.parseFloat js/Number initial-value)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-account/name]
 :<- [::s.e.f.create-account/initial-value]
 :<- [::s.e.f.create-account/currency-id]
 :<- [::s.e.f.create-account/user-id]
 form-data-sub)
(def form-data ::form-data)
