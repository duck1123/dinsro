(ns dinsro.actions.websocket
  (:require
   [clojure.spec.alpha :as s]
   [clojure.java.io :as io]
   [cognitect.transit :as transit]
   [dinsro.streams :as streams]
   [manifold.stream :as ms]
   [mount.core :as mount]
   [org.httpkit.server :refer [send! with-channel on-close on-receive]]
   [taoensso.timbre :as timbre])
  (:import
   java.io.ByteArrayOutputStream))

(defn connect!
  [channel key]
  (timbre/infof "channel open %s: %s" key channel)
  (swap! streams/channels assoc key channel))

(defn disconnect! [_channel key status]
  (timbre/infof "channel %s closed: " key status)
  (swap! streams/channels #(dissoc % key)))

(defn notify-clients
  "Send a message to all connected clients"
  [msg]
  (doseq [[target-key channel] @streams/channels]
    (timbre/infof "Sending to channel %s => %s" target-key msg)
    (let [out (ByteArrayOutputStream. 4096)
          writer (transit/writer out :json)]
      (transit/write writer msg)
      (send! channel (str out)))))

(s/fdef notify-clients
  :args (s/cat :msg any?)
  :ret nil?)

(defn handle-stream-message
  [[_source-key msg]]
  (notify-clients msg))

(defn handle-message
  "Handle messages from the stream"
  [key message]
  (let [in (io/input-stream (.getBytes message))
        reader (transit/reader in :json)
        data (transit/read reader)]
    (timbre/debugf "Received message: %s => %s" key data)

    ;; Echo messages back out
    (ms/put! streams/message-source [key data])))

(mount/defstate ^{:on-reload :noop} consumer
  :start (do
           (timbre/info "Starting consumer")
           (ms/consume handle-stream-message streams/message-source)))

(defn ws-handler
  [request]
  (let [key (get-in request [:headers "sec-websocket-key"])]
    (with-channel request channel
      (connect! channel key)
      (on-close channel (partial disconnect! channel key))
      (on-receive channel (partial handle-message key)))))
