(ns dinsro.components.settings
  (:require
   [day8.re-frame.http-fx]
   [dinsro.events.settings :as e.settings]
   [dinsro.store :as st]
   [kee-frame.core :as kf]
   [reagent.core :as r]
   [taoensso.timbre :as timbre]))

(defn require-settings
  [store body]
  [(r/create-class
    {:display-name "require-settings"
     :component-did-mount
     (fn [_this]
       (st/dispatch store [::e.settings/do-fetch-settings]))
     :reagent-render
     (fn [body]
       (let [status-state @(st/subscribe store [::e.settings/settings-state])]
         (if (= status-state :loaded)
           [:div body]
           [:div "Loaded"])))}) body])

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::settings-state))

  (kf/reg-controller
   :settings-controller
   {:params (constantly true)
    :start [::e.settings/do-fetch-settings]})

  store)
