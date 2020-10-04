(ns dinsro.events.websocket
  (:require
   [cljs.core.async :refer [<! offer!]]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]
   [transit-websocket-client.core :as websocket])
  (:require-macros
   [cljs.core.async.macros :refer [go-loop]]))

(def websocket-channel (atom nil))

(defn create-connection
  [store url fire-event]
  (timbre/infof "Connecting to endpoint: %s" url)
  (let [channel (websocket/async-websocket url)]
    (go-loop []
      (let [data (<! channel)]
        (st/dispatch store [fire-event data])
        (recur)))
    channel))

(defn connect!
  [store _cofx [endpoint]]

  (reset!
   websocket-channel
   (create-connection store endpoint ::receive-message)))

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

(defn init-handlers!
  [store]
  (doto store
    (st/reg-event-fx ::receive-message receive-message)
    (st/reg-event-fx ::send-message send-message)
    (st/reg-event-fx ::connect (partial connect! store)))
  store)
