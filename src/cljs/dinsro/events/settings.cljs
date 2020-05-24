(ns dinsro.events.settings
  (:require
   [dinsro.events :as e]
   [dinsro.spec.events.forms.settings :as s.e.f.settings]
   [kee-frame.core :as kf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::settings-state)

(defn do-fetch-settings-success
  [{:keys [db]} [settings]]
  (let [{:keys [allow-registration]} settings]
    {:db (-> db
             (assoc ::s.e.f.settings/allow-registration allow-registration)
             (assoc ::settings-state :loaded))}))

(defn do-fetch-settings-failure
  [_ _]
  {})

(defn do-fetch-settings
  [{:keys [db]} _]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-settings]
    (:token db)
    [::do-fetch-settings-success]
    [::do-fetch-settings-failure])})

(kf/reg-event-fx ::do-fetch-settings-success do-fetch-settings-success)
(kf/reg-event-fx ::do-fetch-settings-failure do-fetch-settings-failure)
(kf/reg-event-fx ::do-fetch-settings do-fetch-settings)
