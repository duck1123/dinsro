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

;; [[../../../../notebooks/dinsro/notebooks/nostr/connections_notebook.clj]]

;; "a atom holding a map from connection ids to a map holding client and connection"
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
  "on-close handler for channels"
  [chan]
  [ds/channel? => any?]
  (fn [_ws status reason]
    (log/info :on-close/starting {:status status :reason reason})
    (async/close! chan)))

(defn create-client
  "Create and associate client for connection"
  [connection-id]
  (if-let [relay-id (q.n.relays/find-by-connection connection-id)]
    (if-let [relay (q.n.relays/read-record relay-id)]
      (let [address     (::m.n.relays/address relay)
            chan        (async/chan)
            result-atom (atom "")
            params      {:on-message (on-message result-atom chan)
                         :on-close   (on-close chan)}
            client      @(ws/websocket address params)]
        (log/info :create-client/associating {:connection-id connection-id :client client :chan chan})
        (swap! connections assoc connection-id {:client client :chan chan})
        client)
      (throw (ex-info "No relay" {:relay-id relay-id})))
    (throw (ex-info "No relay id" {:connection-id connection-id}))))

(>defn get-client*
  "Read client from connections information"
  [connection-id]
  [::m.n.connections/id => (? ::client)]
  (when-let [existing-connection (get @connections connection-id)]
    (log/info :get-client/cached {:connection-id connection-id})
    (let [client (:client existing-connection)]
      client)))

(>defn get-client
  "Get the websocket client for the given connection id or create if missing"
  [connection-id]
  [::m.n.connections/id => ::client]
  (log/info :get-client/starting {:connection-id connection-id})
  (or (get-client* connection-id)
      (create-client connection-id)))

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

(>defn get-topic-channel*
  "Get the topic channel for the current run"
  [run-id]
  [::m.n.runs/id => (? ds/channel?)]
  (log/info :get-topic-channel*/starting {:run-id run-id})
  (if-let [response (get @topics run-id)]
    response
    (do
      (log/info :get-topic-channel*/not-found {:run-id run-id})
      nil)))

(>defn get-topic-channel
  "Get the topic channel for the current run. Throw if missing"
  [run-id]
  [::m.n.runs/id => ds/channel?]
  (log/info :get-topic-channel/starting {:run-id run-id})
  (or (get-topic-channel* run-id)
      (throw (ex-info "No channel" {}))))

(>defn set-topic-channel
  "Set topic channel for run"
  [run-id ch]
  [::m.n.runs/id ds/channel? => any?]
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
  "Open a websocket for the provided connection"
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

(>defn register-connection!*
  ([relay-id]
   [::m.n.relays/id => (? ::m.n.connections/id)]
   (register-connection!* relay-id true))
  ([relay-id register-if-not-found?]
   [::m.n.relays/id boolean? => (? ::m.n.connections/id)]
   (do
     (log/info :register-connection!/starting {:relay-id relay-id})
     (if-let [connection-id (q.n.connections/find-connected-by-relay relay-id)]
       connection-id
       (when register-if-not-found?
         (let [connection-id (create-connection! relay-id)]
           (start! connection-id)
           connection-id))))))

(>defn register-connection!
  ([relay-id]
   [::m.n.relays/id => ::m.n.connections/id]
   (register-connection! relay-id true))
  ([relay-id register-if-not-found?]
   [::m.n.relays/id boolean? => ::m.n.connections/id]
   (or (register-connection!* relay-id register-if-not-found?)
       (throw (ex-info "Not Registered" {})))))

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
  (if-let [client (get-client connection-id)]
    (do
      (log/debug :disconnect!/closing {:client client})
      (let [response (ws/close! client)]
        (log/debug :disconnect!/closed {:response response
                                        :client client})
        response))

    (do
      (log/error :disconnect!/no-client {:connection-id connection-id})
      nil)))

(comment

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id

  (q.n.connections/find-by-relay relay-id)

  (ds/gen-key ::m.n.connections/item)
  (q.n.connections/index-ids)

  (register-connection! relay-id)

  (map q.n.connections/read-record (q.n.connections/find-connected))

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
