(ns dinsro.actions.websocket
  (:require [org.httpkit.server :refer [send! with-channel on-close on-receive]]
            [taoensso.timbre :as timbre]))

(defonce channels (atom #{}))

(defn connect! [channel]
  (timbre/infof "channel open: %s" channel)
  (swap! channels conj channel))

(defn notify-clients
  [msg]
  (timbre/infof "Notifying: %s" msg)
  (doseq [channel @channels]
    (send! channel msg)))

(defn disconnect! [channel status]
  (timbre/info "channel closed:" status)
  (swap! channels #(remove #{channel} %)))

(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel notify-clients)))
