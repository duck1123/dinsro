(ns dinsro.components.forms.settings
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.forms.settings :as e.f.settings]
   [dinsro.events.settings :as e.settings]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(kf/reg-controller
 :settings-controller
 {:params (constantly true)
  :start [::e.settings/do-fetch-settings]})

(defn form
  []
  (let [form-data @(rf/subscribe [::e.f.settings/form-data])]
    [:div
     [:p "form"]
     [c.debug/debug-box form-data]
     [:label.checkbox]
     (c/checkbox-input
      "Allow Registration"
      ::s.e.f.settings/allow-registration
      ::s.e.f.settings/set-allow-registration)]))

(s/fdef form
  :args (s/cat)
  :ret vector?)
