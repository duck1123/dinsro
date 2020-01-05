(ns dinsro.events.websocket
  (:require [cljs.core.async :refer [<! offer!]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]
            [transit-websocket-client.core :as websocket])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(def websocket-endpoint "ws://localhost:3000/ws")

(defn create-connection
  [url fire-event]
  (let [channel (websocket/async-websocket url)]
    (go-loop []
      (let [data (<! channel)]
        (rf/dispatch [fire-event data])
        (recur)))
    channel))

(def websocket-channel (create-connection websocket-endpoint ::receive-message))

(kf/reg-event-fx
 ::receive-message
 (fn [_ _]
   {}))

(kf/reg-event-fx
 ::send-message
 (fn [_ [data]]
   (offer! websocket-channel data)
   {}))
