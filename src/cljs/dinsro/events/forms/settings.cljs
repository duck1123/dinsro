(ns dinsro.events.forms.settings
  (:require
   [clojure.spec.alpha]
   [dinsro.event-utils :as eu]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(def ns-sym 'dinsro.events.forms.settings)

(eu/declare-form
 ns-sym
 [::s.e.f.settings/allow-registration])

(defn form-data-sub
  [{:keys [::s.e.f.settings/allow-registration]}
   _]
  {:allow-registration allow-registration})

(defn init-handlers!
  [store]
  (doto store
    (eu/register-form ns-sym)
    (st/reg-sub ::form-data form-data-sub))
  store)
