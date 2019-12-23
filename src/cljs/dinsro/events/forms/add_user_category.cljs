(ns dinsro.events.forms.add-user-category
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.events.forms.add-user-category :as s.e.f.add-user-category]
            [dinsro.spec.users :as s.users]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]))

(rfu/reg-basic-sub ::s.e.f.add-user-category/shown?)
(rfu/reg-set-event ::s.e.f.add-user-category/shown?)

(rfu/reg-basic-sub ::s.e.f.add-user-category/name)
(rfu/reg-set-event ::s.e.f.add-user-category/name)

(rfu/reg-basic-sub ::s.e.f.add-user-category/user-id)
(rfu/reg-set-event ::s.e.f.add-user-category/user-id)

(defn create-form-data
  [[name user-id] _]
  {:name          name
   :user-id       (int user-id)})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.add-user-category/name]
 :<- [::s.e.f.add-user-category/user-id]
 create-form-data)
