(ns dinsro.actions.nostr.connections
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.queries.nostr.connections :as q.n.connections]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.queries.nostr.runs :as q.n.runs]
   [dinsro.specs :as ds]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log])
  (:import
   java.nio.HeapCharBuffer))

(defonce connections (atom {}))
(s/def ::client any?)

(>def ::incoming-event (s/keys))

;; :req-id     req-id
;; :tags       tags
;; :id         id
;; :created-at created-at
;; :pubkey     pubkey
;; :kind       kind
;; :sig        sig
;; :content    content

(>def ::req-id string?)
(>def ::tags vector?)
(>def ::id string?)
(>def ::created-at int?)
(>def ::pubkey string?)
(>def ::kind int?)
(>def ::sig string?)
(>def ::content string?)
(>def ::type string?)
(>def ::outgoing-event
  (s/keys :req-un [::req-id ::type]
          :opt-un [::tags ::id ::created-at ::pubkey ::kind ::sig ::content]))

(defonce topics (atom {}))

(>defn handle-message
  [result-atom chan _ws msg last?]
  [ds/atom? ds/channel? any? (ds/instance? HeapCharBuffer) boolean? => any?]
  (if last?
    (let [msg-str (str @result-atom (str msg))
          data    (json/read-str msg-str)]
      (async/put! chan data)
      (reset-vals! result-atom ""))
    (let [msg-str (str @result-atom (str msg))]
      (reset-vals! result-atom msg-str))))

(>defn on-message
  "Takes a chan, returns a message handler"
  [result-atom chan]
  [ds/atom? ds/channel? => any?]
  (partial handle-message result-atom chan))

(>defn on-close
  [chan]
  [ds/channel? => any?]
  (fn [_ws _status _reason]
    (async/close! chan)))

(>defn get-client
  [connection-id]
  [::m.n.connections/id => ::client]
  (log/info :get-client/starting {:connection-id connection-id})
  (if-let [existing-connection (get @connections connection-id)]
    (do
      (log/info :get-client/cached {:connection-id connection-id})
      (:client existing-connection))
    (if-let [relay-id (q.n.relays/find-by-connection connection-id)]
      (if-let [relay (q.n.relays/read-record relay-id)]
        (let [address     (::m.n.relays/address relay)
              chan        (async/chan)
              result-atom (atom "")
              params      {:on-message (on-message result-atom chan)
                           :on-close   (on-close chan)}
              client      @(ws/websocket address params)]
          (log/info :get-client/associating {:connection-id connection-id :client client :chan chan})
          (swap! connections assoc connection-id {:client client :chan chan})
          client)
        (throw (ex-info "No relay" {:relay-id relay-id})))
      (throw (ex-info "No relay id" {:connection-id connection-id})))))

(>defn get-connection-channel
  [connection-id]
  [::m.n.connections/id => ds/channel?]
  (log/info :get-connection-channel/starting {:connection-id connection-id})
  (if-let [response (get @connections connection-id)]
    (:chan response)
    (do
      (log/info :get-connection-channel/not-found {:connection-id connection-id})
      ;; (async/chan)
      (throw (ex-info "No channel" {})))))

(>defn get-topic-channel
  [run-id]
  [::m.n.runs/id => ds/channel?]
  (log/info :get-topic-channel/starting {:run-id run-id})
  (if-let [response (get @topics run-id)]
    response
    (do
      (log/info :get-topic-channel/not-found {:run-id run-id})
      ;; (async/chan)
      (throw (ex-info "No channel" {})))))

(defn set-topic-channel
  [run-id ch]
  (log/info :set-topic-channel/starting {:run-id run-id :ch ch})
  (swap! topics assoc run-id ch))

(>defn create-connection!
  [relay-id]
  [::m.n.relays/id => ::m.n.connections/id]
  (log/info :create-connection!/starting {:relay-id relay-id})
  (q.n.connections/create-record
   {::m.n.connections/relay  relay-id
    ::m.n.connections/status :initial}))

(>defn handle-event
  [type req-id evt]
  [string? string? ::incoming-event => ::outgoing-event]
  (let [{id         "id"
         kind       "kind"
         pubkey     "pubkey"
         created-at "created_at"
         content    "content"
         tags       "tags"
         sig        "sig"} evt
        parsed-event       {:req-id     req-id
                            :tags       tags
                            :id         id
                            :type       type
                            :created-at created-at
                            :pubkey     pubkey
                            :kind       kind
                            :sig        sig
                            :content    content}]
    parsed-event))

(>defn handle-eose
  [type req-id _evt]
  [string? string? any? => ::outgoing-event]
  {:req-id req-id
   :type type})

(>defn parse-message
  "Parse a response message"
  [message]
  [vector? => any?]
  #_(log/trace :parse-message/starting {:message message})
  (let [[type req-id evt] message]
    #_(log/trace :parse-message/starting {:req-id req-id :type type})
    (condp = type
      "EVENT" (handle-event type req-id evt)
      "EOSE"  (handle-eose type req-id evt)
      (do
        (log/warn :parse-message/unknown-type {:type type})
        nil))))

(defn close-all-topics!
  [connection-id]
  (log/info :close-all-topics!/starting {:connection-id connection-id})
  (let [runs (q.n.runs/find-active-by-connection connection-id)]
    (log/info :close-all-topics!/runs {:runs runs})
    (doseq [run runs]
      (log/info :close-all-topics!/run {:run run}))))

(defn start!
  [connection-id]
  (log/info :start!/starting {:connection-id connection-id})
  (q.n.connections/set-connecting! connection-id)
  (if-let [client (get-client connection-id)]
    (let [chan (get-connection-channel connection-id)]
      (q.n.connections/set-connected! connection-id)
      (async/go-loop []
        (if-let [msg (async/<! chan)]
          (do
            (log/info :start!/received {:msg msg})
            (let [[_event-type code _body] msg]
              (if-let [run-id (q.n.runs/find-active-by-connection-and-code connection-id code)]
                (let [topic-channel (get-topic-channel run-id)]
                  (if-let [parsed-message (parse-message msg)]
                    (do
                      (log/info :start!/parsed {:parsed-message parsed-message})
                      (async/put! topic-channel parsed-message))
                    (log/info :start!/no-msg {:msg msg}))
                  (recur))
                (do
                  (log/error :start!/no-run {:connection-id connection-id :code code})
                  nil))))
          (do
            (log/info :start!/closed {})
            (close-all-topics! connection-id)
            (q.n.connections/set-disconnected! connection-id))))
      client)
    (do
      (q.n.connections/set-errored! connection-id)
      (throw (ex-info "Failed to create client" {})))))

(>defn register-connection!
  [relay-id]
  [::m.n.relays/id => ::m.n.connections/id]
  (log/info :register-connection!/starting {:relay-id relay-id})
  (if-let [connection-id (q.n.connections/find-connected-by-relay relay-id)]
    connection-id
    (let [connection-id (create-connection! relay-id)]
      (start! connection-id)
      connection-id)))

(defn stop!
  [relay-id]
  (log/info :stop!/starting {:relay-id relay-id})
  (throw (ex-info "" {})))

(>defn send!
  [connection-id message]
  [::m.n.connections/id string? => nil?]
  (log/info :send!/starting {:connection-id connection-id :message message})
  (let [client (start! connection-id)]
    (log/info :send!/sending {:client client})
    (ws/send! client message)
    nil))
(defn disconnect!
  [connection-id]
  (log/info :disconnect!/starting {:connection-id connection-id})
  (let [client (get-client connection-id)]
    (ws/close! client)))

(comment

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id

  (q.n.connections/find-by-relay relay-id)

  (ds/gen-key ::m.n.connections/item)
  (q.n.connections/index-ids)

  (register-connection! relay-id)

  (def connection-id (first (q.n.connections/find-connected)))
  connection-id

  (q.n.connections/set-disconnected! connection-id)

  (q.n.connections/read-record connection-id)
  (disconnect! connection-id)

  (some-> (q.n.relays/find-by-connection connection-id)
          q.n.relays/read-record)

  (start! connection-id)

  (get-client connection-id)

  (def code (::m.n.requests/code (q.n.requests/read-record (first (q.n.requests/index-ids)))))

  (q.n.runs/index-ids)
  (q.n.runs/find-active)

  (q.n.runs/find-active-by-connection connection-id)
  (q.n.runs/find-active-by-code code)

  (q.n.runs/find-active-by-connection-and-code connection-id code)

  (q.n.connections/delete! connection-id)
  (q.n.connections/read-record connection-id)

  (q.n.connections/set-connecting! connection-id)
  (q.n.connections/set-connected! connection-id)
  (q.n.connections/set-disconnected! connection-id)
  (q.n.connections/set-errored! connection-id)

  nil)
