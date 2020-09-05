(ns dinsro.components.settings
  (:require
   [day8.re-frame.http-fx]
   [dinsro.events.settings :as e.settings]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reagent.core :as r]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(rfu/reg-basic-sub ::settings-state)

(defn require-settings
  [body]
  [(r/create-class
     {:display-name "require-settings"
      :component-did-mount
      (fn [_this]
        (rf/dispatch [::e.settings/do-fetch-settings]))
      :reagent-render
      (fn [body]
        (let [status-state @(rf/subscribe [::e.settings/settings-state])]
          (if (= status-state :loaded)
            [:div body]
            [:div "Loaded"])))}) body])

(comment
  (kf/reg-controller
   :status-controller
   {:params (constantly true)
    :start [::e.settings/do-fetch-settings]}))
