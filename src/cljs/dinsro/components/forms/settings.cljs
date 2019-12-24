(ns dinsro.components.forms.settings
  (:require [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.settings :as e.f.settings]
            [dinsro.spec.events.forms.settings :as s.e.f.settings]
            [dinsro.translations :refer [tr]]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn-spec form vector?
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
