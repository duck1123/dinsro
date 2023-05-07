(ns dinsro.actions.nostr.relays
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.actions.nostr.connections :as a.n.connections]
   [dinsro.actions.nostr.relay-client :as a.n.relay-client]
   [dinsro.actions.nostr.requests :as a.n.requests]
   [dinsro.model.nostr.relays :as m.n.relays]
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
                       (if-let [parsed-message (a.n.connections/parse-message msg)]
                         (async/put! topic-channel parsed-message)
                         (log/finest :get-client-for-id/no-msg {:msg msg}))
                       topic-channel
                       (recur))
                     (log/finest :get-client-for-id/closed {})))

                 client)
               (throw (ex-info "Failed to create client" {}))))
           (a.n.relay-client/get-client-for-address address)))
       (throw (ex-info "Failed to find relay" {}))))))

;; body is a map that will be turned into a message

(>defn connect!
  "Connect to relay and store connection information"
  [relay-id]
  [::m.n.relays/id => (? ::client)]
  (log/trace :connect!/starting {:relay-id relay-id})
  (q.n.relays/set-connected relay-id true)
  (if-let [client   (get-client-for-id relay-id false)]
    (do
      (log/trace :connect!/finished {:client client})
      client)
    (do
      (log/info :connect!/creating {})
      (let [client (get-client-for-id relay-id true)]
        ;; (process-relay-messages relay-id)
        client))))

(>defn send!
  "Send a message to a relay"
  ([relay-id body]
   [::m.n.relays/id any? => ds/channel?]
   (let [code (a.n.requests/get-next-code!)]
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

(>defn register-relay!
  [address]
  [::m.n.relays/address => ::m.n.relays/id]
  (log/info :register-relay/starting {:address address})
  (if-let [relay-id (q.n.relays/find-by-address address)]
    relay-id
    (let [params {::m.n.relays/address   address
                  ::m.n.relays/connected false}]
      (q.n.relays/create-record params))))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))

  (send! relay-id
         {:kinds [3]
          :authors ["6fe701bde348f57e1068101830ad2015f32d3d51d0d685ff0f2812ee8635efec"]})

  (q.n.relays/create-connected-toggle)

  (q.n.relays/read-record relay-id)

  (connect! relay-id)
  (disconnect! relay-id)

  (map q.n.relays/read-record (q.n.relays/index-ids))

  (some->
   (q.n.relays/index-ids)
   first
   q.n.relays/read-record)

  nil)
