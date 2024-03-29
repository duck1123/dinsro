(ns dinsro.actions.nostr.relay-client
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.specs :as ds]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log])
  (:import
   java.nio.HeapCharBuffer))

(defonce connections (atom {}))
(def timeout-time 10000)
(s/def ::client any?)

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
  [result-atom chan _ws msg last?]
  [ds/atom? ds/channel? any? (ds/instance? HeapCharBuffer) boolean? => any?]
  (log/trace :handle-message/started {:msg msg :last? last?})
  (if last?
    (let [msg-str                      (str @result-atom (str msg))
          o                            (json/read-str msg-str)
          [event-type request-id body] o]
      (log/trace :handle-message/received {:event-type event-type
                                           :body       body
                                           :request-id request-id
                                           :chan       chan})
      (async/put! chan o)
      (reset-vals! result-atom ""))
    (let [msg-str (str @result-atom (str msg))]
      (log/debug :handle-message/enqueueing {:msg-str msg-str})
      (reset-vals! result-atom msg-str))))

(>defn on-message
  "Takes a chan, returns a message handler"
  [result-atom chan]
  [ds/atom? ds/channel? => any?]
  (log/info :on-message/starting {:chan chan})
  (partial handle-message result-atom chan))

(>defn on-close
  [url chan]
  [string? ds/channel? => any?]
  (fn [_ws _status _reason]
    (log/info :on-closed/received {:url url})
    (async/close! chan)))

(>defn get-client
  [chan url]
  [ds/channel? string? => ::client]
  (if-let [existing-connection (get @connections url)]
    (do
      (log/info :get-client/cached {:url url})
      (:client existing-connection))
    (do
      (log/info :get-client/opening {:url url})
      (let [result-atom (atom "")
            params      {:on-message (on-message result-atom chan)
                         :on-close   (on-close url chan)}
            client      @(ws/websocket url params)]
        (swap! connections assoc url {:client client :chan chan})
        client))))

(>defn get-channel
  "Returns a channel for a relay address"
  [address]
  [string? => ds/channel?]
  (if-let [item (get @connections address)]
    (:chan item)
    (throw (ex-info "No channel" {}))))

(>defn get-client-for-address
  [address]
  [string? => (? ::client)]
  (if-let [client (get-in @connections [address :client])]
    (do
      (log/trace :get-client-for-address/cached {:client client})
      client)
    (do
      (log/trace :get-client-for-address/missing {})
      nil)))

(defn send!
  [client request-id body]
  (log/trace :send!/starting {:client client :request-id request-id :body body})
  (let [chan (async/chan)]
    (let [message (json/json-str ["REQ" request-id body])]
      (ws/send! client message))
    chan))
