(ns dinsro.events.forms.settings
  (:require
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn form-data-sub
  [{:keys [::s.e.f.settings/allow-registration]}
   _]
  {:allow-registration allow-registration})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::s.e.f.settings/allow-registration)
    (st/reg-set-event ::s.e.f.settings/allow-registration)
    (st/reg-basic-sub ::s.e.f.settings/first-run)
    (st/reg-set-event ::s.e.f.settings/first-run)
    (st/reg-sub ::form-data form-data-sub))
  store)
