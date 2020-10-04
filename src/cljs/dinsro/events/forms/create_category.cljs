(ns dinsro.events.forms.create-category
  (:require
   [clojure.spec.alpha]
   [dinsro.event-utils :as eu]
   [dinsro.spec.actions.categories :as s.a.categories]
   [dinsro.spec.events.forms.create-category :as s.e.f.create-category]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.create-category)

(eu/declare-form
 ns-sym
 ::s.a.categories/create-params-valid
 [::s.e.f.create-category/name
  ::s.e.f.create-category/user-id])

(defn form-data-sub
  [{:keys [::s.e.f.create-category/name
           ::s.e.f.create-category/user-id]}
   _]
  {:name          name
   :user-id       (int user-id)})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-form ns-sym)
    (st/reg-sub ::form-data form-data-sub))
  store)
