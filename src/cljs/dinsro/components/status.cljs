(ns dinsro.components.status
  (:require [ajax.core :as ajax]
            [day8.re-frame.http-fx]
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
  (timbre/info "init")
  {:http-xhrio
   {:uri "/api/v1/status"
    :method :get
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success [:status-loaded]
    :on-failure [:status-errored]}})

(kf/reg-event-fx :init-status init-status)

(kf/reg-controller
 :status-controller
 {:params (constantly true)
  :start [:init-status]})

(defn require-status
  [body]
  body)
