(ns dinsro.components.forms.settings
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.components.debug :as c.debug]
   [dinsro.events.forms.settings :as e.f.settings]
   [dinsro.events.settings :as e.settings]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(comment
  (kf/reg-controller
   :settings-controller
   {:params (constantly true)
    :start [::e.settings/do-fetch-settings]}))

(defn form
  [store]
  (let [form-data @(st/subscribe store [::e.f.settings/form-data])]
    [:div
     [:p "form"]
     [c.debug/debug-box store form-data]
     [:label.checkbox]
     (c/checkbox-input
      store
      "Allow Registration"
      ::s.e.f.settings/allow-registration
      ::s.e.f.settings/set-allow-registration)]))

(s/fdef form
  :args (s/cat)
  :ret vector?)
