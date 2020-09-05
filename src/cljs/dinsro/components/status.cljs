(ns dinsro.components.status
  (:require
   [day8.re-frame.http-fx]
   [dinsro.events :as e]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.websocket :as e.websocket]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(def websocket-endpoint (str "ws://" (.-host (.-location js/window))  "/ws"))

(defn status-loaded
  [{:keys [db]} [{:keys [identity]}]]
  (timbre/info "status loaded")
  {:db (-> db
           (assoc ::e.authentication/auth-id identity)
           (assoc ::status-state :loaded))
   :dispatch-n [[::e.websocket/connect websocket-endpoint]]})

(defn status-errored
  [{:keys [db]} _]
  (timbre/warn "status errored")
  {:db (assoc db ::status-state :errored)})

(rfu/reg-basic-sub ::status-state)


(kf/reg-event-fx :status-loaded status-loaded)
(kf/reg-event-fx :status-errored status-errored)

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
 [(rf/inject-cofx :cookie/get [:token])]
 init-status)

(kf/reg-controller
 :status-controller
 {:params (constantly true)
  :start [:init-status]})

(defn require-status
  [body]
  (let [status-state @(rf/subscribe [::status-state])]
    (if (= status-state :loaded)
      [:div body]
      [:div "Loaded"])))
