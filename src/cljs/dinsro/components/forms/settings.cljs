(ns dinsro.components.forms.settings
  (:require [ajax.core :as ajax]
            [dinsro.components :as c]
            [dinsro.components.debug :as c.debug]
            [dinsro.events.forms.settings :as e.f.settings]
            [dinsro.spec.events.forms.settings :as s.e.f.settings]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))


(kf/reg-event-db
 :settings-loaded
 (fn [db [{:keys [allow-registration]}]]
   (assoc db ::s.e.f.settings/allow-registration allow-registration)))

(defn settings-errored
  [_ _]
  {})

(kf/reg-event-fx :settings-errored settings-errored)

(defn init-settings
  [_ _]
  {:http-xhrio
   {:uri "/api/v1/settings"
    :method :get
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success [:settings-loaded]
    :on-failure [:settings-errored]}})

(kf/reg-event-fx :init-settings init-settings)

(kf/reg-controller
 :settings-controller
 {:params (constantly true)
  :start [:init-settings]})

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
