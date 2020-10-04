(ns dinsro.events.forms.settings
  (:require
   [clojure.spec.alpha]
   [dinsro.event-utils :as eu]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.settings)

(defn form-data-sub
  [{:keys [::s.e.f.settings/allow-registration]}
   _]
  {:allow-registration allow-registration})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-form ns-sym)
    (st/reg-basic-sub ::s.e.f.settings/allow-registration)
    (st/reg-set-event ::s.e.f.settings/allow-registration)
    (st/reg-basic-sub ::s.e.f.settings/first-run)
    (st/reg-set-event ::s.e.f.settings/first-run)
    (st/reg-sub ::form-data form-data-sub))
  store)
