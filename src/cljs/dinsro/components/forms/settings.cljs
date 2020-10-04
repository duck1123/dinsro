(ns dinsro.components.forms.settings
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.components :as c]
   [dinsro.events.settings :as e.settings]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
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
  [:div
   [:p "form"]
   [:label.checkbox]
   (c/checkbox-input
    store
    "Allow Registration"
    ::s.e.f.settings/allow-registration
    ::s.e.f.settings/set-allow-registration)])

(s/fdef form
  :args (s/cat)
  :ret vector?)
