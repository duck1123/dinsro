(ns dinsro.events.forms.add-user-category
  (:require
   [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn form-data-sub
  [{:keys [::s.e.f.create-category/name
           ::s.e.f.create-category/user-id]}

   _]
  {:name          name
   :user-id       (int user-id)})

(rf/reg-sub ::form-data form-data-sub)
(def form-data ::form-data)
