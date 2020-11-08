(ns dinsro.events.settings
  (:require
   [dinsro.events :as e]
   [dinsro.specs.events.forms.settings :as s.e.f.settings]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(defn do-fetch-settings-success
  [_store {:keys [db]} [settings]]
  (let [{:keys [allow-registration]} settings]
    {:db (-> db
             (assoc ::s.e.f.settings/allow-registration allow-registration)
             (assoc ::settings-state :loaded))}))

(defn do-fetch-settings-failure
  [_store _cofx _event]
  {})

(defn do-fetch-settings
  [store {:keys [db]} _event]
  {:http-xhrio
   (e/fetch-request-auth
    [:api-settings]
    store
    (:token db)
    [::do-fetch-settings-success]
    [::do-fetch-settings-failure])})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::settings-state)
    (st/reg-event-fx ::do-fetch-settings-success (partial do-fetch-settings-success store))
    (st/reg-event-fx ::do-fetch-settings-failure (partial do-fetch-settings-failure store))
    (st/reg-event-fx ::do-fetch-settings (partial do-fetch-settings store)))
  store)
