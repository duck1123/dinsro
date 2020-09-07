(ns dinsro.components.status
  (:require
   [day8.re-frame.http-fx]
   [dinsro.events :as e]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.events.websocket :as e.websocket]
   [dinsro.store :as st]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
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

(defn require-status
  [store body]
  (let [status-state @(st/subscribe store [::status-state])]
    (if (= status-state :loaded)
      [:div body]
      [:div "Loading Status"])))

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::status-state)
    (st/reg-set-event ::status-state)
    (st/reg-event-fx :status-loaded status-loaded)
    (st/reg-event-fx :status-errored status-errored)
    (st/reg-event-fx :init-status [(rf/inject-cofx :cookie/get [:token])] init-status))

  (kf/reg-controller
   :status-controller
   {:params (constantly true)
    :start [:init-status]})

  store)
