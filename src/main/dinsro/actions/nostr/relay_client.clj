(ns dinsro.actions.nostr.relay-client
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.specs :as ds]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

(defonce connections (atom {}))
(def req-id "5022")

(def timeout-time 10000)

(>defn take-timeout
  "Read from a channel with a timeout"
  [chan]
  [ds/channel? => ds/channel?]
  (async/go
    (let [[v c] (async/alts! [chan (async/timeout timeout-time)])]
      (if (= c chan)
        v
        (do
          (comment (async/close! chan))
          :timeout)))))

(>defn handle-message
  [chan _ws msg _last?]
  [ds/channel? any? any? any? => any?]
  (let [msg-str (str msg)
        o       (json/read-str msg-str)]
    (log/fine :handle-message/received {:o o})
    (async/put! chan o)))

(>defn on-message
  "Takes a chan, returns a message handler"
  [chan]
  [ds/channel? => any?]
  (partial handle-message chan))

(>defn on-close
  [chan]
  [ds/channel? => any?]
  (fn [_ws _status _reason]
    (log/info :on-closed/received {})
    (async/close! chan)))

(>defn get-client
  [chan url]
  [ds/channel? string? => any?]
  (if-let [existing-connection (get @connections url)]
    (do
      (log/info :get-client/cached {:url url})
      (:client existing-connection))
    (do
      (log/info :get-client/opening {:url url})
      (let [client @(ws/websocket url
                                  {:on-message (on-message chan)
                                   :on-close   (on-close chan)})]
        (swap! connections assoc url {:client client :chan chan})
        client))))

(>defn get-channel
  "Returns a channel for a relay address"
  [address]
  [string? => ds/channel?]
  (if-let [item (get @connections address)]
    (:chan item)
    (throw (RuntimeException. "No channel"))))

(>defn get-client-for-address
  [address]
  [string? => (? ds/channel?)]
  (if-let [client (get-in @connections [address :client])]
    (do
      (log/info :get-client-for-id/cached {:client client})
      client)
    (do
      (log/info :get-client-for-id/missing {})
      nil)))

(defn send!
  [client request-id body]
  (log/info :send!/starting {:client client :request-id request-id :body body})
  (let [message    (json/json-str ["REQ" request-id body])]
    (ws/send! client message)))
