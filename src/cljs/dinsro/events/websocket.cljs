(ns dinsro.events.websocket
  (:require
   [cljs.core.async :refer [<! offer!]]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]
   [transit-websocket-client.core :as websocket])
  (:require-macros
   [cljs.core.async.macros :refer [go-loop]]))

(def websocket-channel (atom nil))

(defn create-connection
  [url fire-event]
  (timbre/infof "Connecting to endpoint: %s" url)
  (let [channel (websocket/async-websocket url)]
    (go-loop []
      (let [data (<! channel)]
        (rf/dispatch [fire-event data])
        (recur)))
    channel))

(defn connect
  [_ [endpoint]]
  (reset! websocket-channel (create-connection endpoint ::receive-message))
  {})

(defn receive-message
  [_ [data]]
  {:dispatch data})

(defn send-message
  [_ [data]]
  (if-let [ch @websocket-channel]
    (do
      (offer! ch data)
      {})
    (throw "Channel not opened")))

(kf/reg-event-fx ::receive-message receive-message)
(kf/reg-event-fx ::send-message send-message)
(kf/reg-event-fx ::connect connect)
