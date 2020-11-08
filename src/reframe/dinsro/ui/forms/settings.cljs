(ns dinsro.ui.forms.settings
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.events.settings :as e.settings]
   [dinsro.specs.events.forms.settings :as s.e.f.settings]
   [dinsro.translations :refer [tr]]
   [dinsro.ui :as u]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(kf/reg-controller
 :settings-controller
 {:params (constantly true)
  :start [::e.settings/do-fetch-settings]})

(defn form
  [store]
  [:div
   [:label.checkbox]
   (u/checkbox-input
    store
    "Allow Registration"
    ::s.e.f.settings/allow-registration
    ::s.e.f.settings/set-allow-registration)])

(s/fdef form
  :args (s/cat)
  :ret vector?)
