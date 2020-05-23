(ns dinsro.events.settings
  (:require
   [dinsro.events :as e]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [kee-frame.core :as kf]
   [taoensso.timbre :as timbre]))

(defn do-fetch-settings-success
  [{:keys [db]} [settings]]
  (let [{:keys [allow-registration]} settings]
    {:db (assoc db ::s.e.f.settings/allow-registration allow-registration)}))

(defn do-fetch-settings-failure
  [_ _]
  {})

(defn do-fetch-settings
  [_ _]
  {:http-xhrio
   (e/fetch-request
    [:api-settings]
    [::do-fetch-settings-success]
    [::do-fetch-settings-failure])})

(kf/reg-event-fx ::do-fetch-settings-success do-fetch-settings-success)
(kf/reg-event-fx ::do-fetch-settings-failure do-fetch-settings-failure)
(kf/reg-event-fx ::do-fetch-settings do-fetch-settings)
