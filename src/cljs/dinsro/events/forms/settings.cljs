(ns dinsro.events.forms.settings
  (:require
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::s.e.f.settings/allow-registration)
(rfu/reg-set-event ::s.e.f.settings/allow-registration)

(defn form-data-sub
  [allow-registration _]
  {:allow-registration allow-registration})

(rf/reg-sub
 ::form-data
 :<- [::s.e.f.settings/allow-registration]
 form-data-sub)
