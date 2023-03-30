(ns dinsro.actions.nostr.relays
  (:require
   [clojure.core.async :as async]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.actions.nostr.relay-client :as a.n.relay-client]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.specs :as ds]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/relays.cljc][Relay Joins]]
;; [[../../model/nostr/relays.cljc][Relay Models]]
;; [[../../mutations/nostr/relays.cljc][Relay Mutations]]
;; [[../../queries/nostr/relays.clj][Relay Queries]]
;; [[../../ui/nostr/relays.cljs][Relay UI]]

(>def ::client any?)

(defonce topic-channels (atom {}))

(defn get-channel
  [relay-id request-id]
  (if-let [chan (get-in @topic-channels [relay-id request-id])]
    chan
    (let [output-chan (async/chan)]
      (swap! topic-channels assoc-in [relay-id request-id] output-chan)
      output-chan)))

(defn get-relay-channel
  "Returns the main channel for messages from a relay"
  [relay-id]
  (log/info :get-relay-channel/starting {})
  (if-let [relay (q.n.relays/read-record relay-id)]
    (let [address (::m.n.relays/address relay)]
      (a.n.relay-client/get-channel address))
    (throw (RuntimeException. "Failed to find relay"))))

(>def ::incoming-event (s/keys))
(>def ::outgoing-event (s/keys))

(>defn handle-event
  [req-id evt]
  [string? ::incoming-event => ::outgoing-event]
  (log/finer :handle-event/starting {:evt evt})
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
                            :created-at created-at
                            :pubkey     pubkey
                            :kind       kind
                            :sig        sig
                            :content    content}]
    (log/info :handle-event/finished {:parsed-event parsed-event})
    parsed-event))

(defn handle-eose
  [req-id evt]
  (log/info :handle-eose/starting {:req-id req-id :evt evt})
  nil)

(defn parse-message
  "Parse a response message"
  [message]
  (log/finer :parse-message/starting {:message message})
  (let [[type req-id evt] message]
    (log/finer :parse-message/starting {:req-id req-id :type type})
    (condp = type
      "EVENT" (handle-event req-id evt)
      "EOSE"  (handle-eose req-id evt)
      (do
        (log/warn :parse-message/unknown-type {:type type})
        nil))))

(>defn get-client-for-id
  ([relay-id]
   [::m.n.relays/id => ::client]
   (get-client-for-id relay-id true))
  ([relay-id create-if-missing?]
   [::m.n.relays/id boolean? => (? ::client)]
   (do
     (log/finest :get-client-for-id/starting {:relay-id          relay-id
                                              :create-if-missing create-if-missing?})
     (if-let [relay (q.n.relays/read-record relay-id)]
       (let [address (::m.n.relays/address relay)]
         (if create-if-missing?
           (let [chan (async/chan)]
             (if-let [client (a.n.relay-client/get-client chan address)]
               (do
                 (log/finest :get-client-for-id/created {})
                 (async/go-loop []
                   (log/finest :get-client-for-id/looping {})
                   (if-let [msg (async/<! chan)]
                     (let [[event-type request-id body] msg
                           topic-channel                (get-channel relay-id request-id)]
                       (log/finest :get-client-for-id/received {:event-type    event-type
                                                                :request-id    request-id
                                                                :body          body
                                                                :topic-channel topic-channel})
                       (if-let [parsed-message (parse-message msg)]
                         (async/put! topic-channel parsed-message)
                         (log/finest :get-client-for-id/no-msg {:msg msg}))
                       topic-channel
                       (recur))
                     (log/finest :get-client-for-id/closed {})))

                 client)
               (throw (RuntimeException. "Failed to create client"))))
           (if-let [client (a.n.relay-client/get-client-for-address address)]
             client
             nil
             #_(throw (RuntimeException. "Failed to find client")))))
       (throw (RuntimeException. "Failed to find relay"))))))

;; body is a map that will be turned into a message

(defonce request-counter (atom 0))

(>defn process-relay-messages
  [relay-id]
  [::m.n.relays/id => any?]
  (let [chan (get-relay-channel relay-id)]
    (async/go-loop []
      (log/info :process-relay-messages/looping {:relay-id relay-id})
      (let [msg (async/<! chan)]
        (log/info :process-relay-messages/received {:msg msg})
        (when-let [parsed-message (parse-message msg)]
          (let [request-id     (:req-id parsed-message)
                channel        (get-channel relay-id request-id)]
            (log/info :process-relay-messages/parsed {:relay-id       relay-id
                                                      :request-id     request-id
                                                      :parsed-message parsed-message})
            (async/put! channel parsed-message)
            (recur)))))))

(>defn connect!
  "Connect to relay and store connection information"
  [relay-id]
  [::m.n.relays/id => (? ::client)]
  (log/finer :connect!/starting {:relay-id relay-id})
  (q.n.relays/set-connected relay-id true)
  (if-let [client   (get-client-for-id relay-id false)]
    (do
      (log/finer :connect!/finished {:client client})
      client)
    (do
      (log/info :connect!/creating {})
      (let [client (get-client-for-id relay-id true)]
        ;; (process-relay-messages relay-id)
        client))))

(defn get-next-code!
  []
  (let [code (str "adhoc " @request-counter)]
    (swap! request-counter inc)
    code))

(>defn send!
  "Send a message to a relay"
  ([relay-id body]
   [::m.n.relays/id any? => ds/channel?]
   (let [code (get-next-code!)]
     (send! relay-id code body)))
  ([relay-id code body]
   [::m.n.relays/id string? any? => ds/channel?]
   (do
     (log/info :send!/starting {:relay-id relay-id :body body})
     (let [client        (connect! relay-id)
           topic-channel (get-channel relay-id code)]
       ;; (log/info :send!/topic {:topic-channel topic-channel})
       (a.n.relay-client/send! client code body)
       topic-channel))))

(>defn disconnect!
  "Disconnect from relay and clear connection information"
  [relay-id]
  [::m.n.relays/id => any?]
  (log/info :disconnect!/starting {:relay-id relay-id})
  (let [response (q.n.relays/set-connected relay-id false)
        relay    (q.n.relays/read-record relay-id)
        url      (::m.n.relays/address relay)
        client   (get-client-for-id relay-id false)]
    (if client
      (ws/close! client)
      (log/warn :disconnect/no-connection {}))
    (swap! a.n.relay-client/connections dissoc url)
    (log/info :disconnect!/finished {:response response :client client})
    response))

(>defn toggle-relay!
  "Toggle state of relay"
  [relay]
  [::m.n.relays/item => any?]
  (log/info :toggle-relay!/starting {:relay relay})
  (let [{::m.n.relays/keys [connected]
         relay-id          ::m.n.relays/id} relay]
    (if connected
      (disconnect! relay-id)
      (connect! relay-id))))

(>defn toggle!
  "Toggle state of relay identified by id"
  [relay-id]
  [::m.n.relays/id => any?]
  (log/info :toggle!/starting {:relay-id relay-id})
  (let [relay    (q.n.relays/read-record relay-id)
        response (toggle-relay! relay)]
    (log/info :toggle!/finished {:response response})
    response))

(defn do-delete!
  [props]
  (log/info :do-delete!/starting {:props props}))

(>defn do-toggle!
  "Handler for toggle! mutation"
  [props]
  [::m.n.relays/ident => any?]
  (log/info :do-toggle!/starting {:props props})
  (let [relay-id (::m.n.relays/id props)
        response (toggle! relay-id)]
    (log/info :do-toggle!/finished {:response response})
    (let [relay (q.n.relays/read-record relay-id)]
      {::mu/status       :ok
       ::m.n.relays/item relay})))

(>defn register-relay!
  [address]
  [::m.n.relays/address => ::m.n.relays/id]
  (log/info :register-relay/starting {:address address})
  (if-let [relay-id (q.n.relays/find-by-address address)]
    relay-id
    (let [params {::m.n.relays/address   address
                  ::m.n.relays/connected false}]
      (q.n.relays/create-record params))))

(defn do-submit!
  [props]
  (log/info :do-submit!/starting {:props props})
  (let [address (::m.n.relays/address props)
        relay-id (register-relay! address)]
    {::mu/status       :ok
     ::m.n.relays/item (q.n.relays/read-record relay-id)}))

(defn do-fetch-events!
  [props]
  (log/info :do-fetch-events!/starting {:props props})
  (let [{relay-id  ::m.n.relays/id
         pubkey-id ::m.n.pubkeys/id} props]
    (log/info :do-fetch-events!/starting {:relay-id  relay-id
                                          :pubkey-id pubkey-id})))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))

  (send! relay-id
         {:kinds [3]
          :authors ["6fe701bde348f57e1068101830ad2015f32d3d51d0d685ff0f2812ee8635efec"]})

  (q.n.relays/create-connected-toggle)

  (q.n.relays/initialize-queries!)

  (q.n.relays/read-record relay-id)

  (connect! relay-id)
  (disconnect! relay-id)

  (map q.n.relays/read-record (q.n.relays/index-ids))

  (some->
   (q.n.relays/index-ids)
   first
   q.n.relays/read-record)

  nil)
