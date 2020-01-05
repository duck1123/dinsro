(ns dinsro.actions.websocket
  (:require
   [clojure.java.io :as io]
   [cognitect.transit :as transit]
   [manifold.stream :as ms]
   [mount.core :as mount]
   [org.httpkit.server :refer [send! with-channel on-close on-receive]]
   [taoensso.timbre :as timbre])
  (:import
   java.io.ByteArrayOutputStream))

(defonce channels (atom {}))
(defonce message-source (ms/stream))

(defn connect!
  [channel key]
  (timbre/infof "channel open %s: %s" key channel)
  (swap! channels assoc key channel))

(defn disconnect! [_channel key status]
  (timbre/infof "channel %s closed: " key status)
  (swap! channels #(dissoc % key)))

(defn notify-clients
  [_source-key msg]
  (doseq [[target-key channel] @channels]
    (timbre/infof "Sending to channel %s => %s" target-key msg)
    (let [out (ByteArrayOutputStream. 4096)
          writer (transit/writer out :json)]
      (transit/write writer msg)
      (send! channel (str out)))))

(defn handle-stream-message
  [[key msg]]
  (notify-clients key msg))

(defn handle-message
  [key message]
  (let [in (io/input-stream (.getBytes message))
        reader (transit/reader in :json)
        data (transit/read reader)]
    (timbre/debugf "Received message: %s => %s" key data)
    (ms/put! message-source [key data])))

(declare consumer)
(mount/defstate ^{:on-reload :noop} consumer
  :start (do
           (timbre/info "Starting consumer")
           (ms/consume handle-stream-message message-source)))

(defn ws-handler
  [request]
  (let [key (get-in request [:headers "sec-websocket-key"])]
    (with-channel request channel
      (connect! channel key)
      (on-close channel (partial disconnect! channel key))
      (on-receive channel (partial handle-message key)))))
