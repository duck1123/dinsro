(ns dinsro.components.status
  (:require [day8.re-frame.http-fx]
            [dinsro.events :as e]
            [dinsro.events.authentication :as e.authentication]
            [kee-frame.core :as kf]
            [taoensso.timbre :as timbre]))

(kf/reg-event-db
 :status-loaded
 (fn [db [{:keys [identity]}]]
   (assoc db ::e.authentication/auth-id identity)))

(kf/reg-event-fx
 :status-errored
 (fn [_ _]
   (timbre/warn "status errored")))

(defn init-status
  [_ _]
  {:http-xhrio
   (e/fetch-request [:api-show-account]
                    [:status-loaded]
                    [:status-errored])})

(kf/reg-event-fx :init-status init-status)

(kf/reg-controller
 :status-controller
 {:params (constantly true)
  :start [:init-status]})

(defn require-status
  [body]
  body)
