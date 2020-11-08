(ns dinsro.ui.status
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
  [_store {:keys [db]} [{:keys [identity]}]]
  (timbre/info "status loaded")
  {:db (-> db
           (assoc ::e.authentication/auth-id identity)
           (assoc ::status-state :loaded))
   :dispatch-n [[::e.websocket/connect websocket-endpoint]]})

(defn status-errored
  [_store {:keys [db]} _]
  (timbre/warn "status errored")
  {:db (assoc db ::status-state :errored)})

(defn init-status
  [store {:keys [db]
    cookies :cookie/get} _]
  (let [token (or (:token db) (:token cookies))]
    {:db (assoc db :token token)
     :http-xhrio
     (e/fetch-request-auth
      [:api-status]
      store
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
    (st/reg-event-fx :status-loaded (partial status-loaded store))
    (st/reg-event-fx :status-errored (partial status-errored store))
    (st/reg-event-fx :init-status [(rf/inject-cofx :cookie/get [:token])] (partial init-status store)))

  (kf/reg-controller
   :status-controller
   {:params (constantly true)
    :start [:init-status]})

  store)
