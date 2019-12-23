(ns dinsro.events.forms.create-category
  (:require [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.create-category/name)
(rfu/reg-set-event ::s.e.f.create-category/name)

(rfu/reg-basic-sub ::s.e.f.create-category/user-id)
(rfu/reg-set-event ::s.e.f.create-category/user-id)

(rfu/reg-basic-sub ::s.e.f.create-category/shown?)
(rfu/reg-set-event ::s.e.f.create-category/shown?)

(defn form-data-sub
  [[name user-id] _]
  {:name          name
   :user-id       (int user-id)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-category/name]
 :<- [::s.e.f.create-category/user-id]
 form-data-sub)
