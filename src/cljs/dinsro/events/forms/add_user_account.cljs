(ns dinsro.events.forms.add-user-account
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.events.forms.add-user-account :as s.e.f.add-user-account]
            [dinsro.spec.events.forms.create-account :as s.e.f.create-account]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(def default-name "Offshore")

(defn form-data-sub
  [form-bindings _]
  (let [[name initial-value currency-id user-id] form-bindings]
    {:name          name
     :currency-id   (int currency-id)
     :user-id       (int user-id)
     :initial-value (.parseFloat js/Number initial-value)}))

(s/fdef form-data-sub
  :args (s/cat :form-bindings ::s.e.f.add-user-account/form-bindings
               :event any?)
  :ret (s/keys))

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.create-account/name]
 :<- [::s.e.f.create-account/initial-value]
 :<- [::s.e.f.create-account/currency-id]
 :<- [::s.e.f.create-account/user-id]
 form-data-sub)
(def form-data ::form-data)
