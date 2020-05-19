(ns dinsro.components.status
  (:require
   [day8.re-frame.http-fx]
   [dinsro.events :as e]
   [dinsro.events.authentication :as e.authentication]
   [kee-frame.core :as kf]
   [re-frame.core :as r]
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
  [{:keys [db]
    cookies :cookie/get} _]
  (let [token (or (:token db) (:token cookies))]
    {:db (assoc db :token token)
     :http-xhrio
     (e/fetch-request-auth
      [:api-status]
      token
      [:status-loaded]
      [:status-errored])}))

(kf/reg-event-fx
 :init-status
 [(r/inject-cofx :cookie/get [:token])]
 init-status)

(kf/reg-controller
 :status-controller
 {:params (constantly true)
  :start [:init-status]})

(defn require-status
  [body]
  body)
