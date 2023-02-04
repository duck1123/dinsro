(ns dinsro.actions.nostr.relays
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.actions.nostr.relay-client :as a.n.relay-client]
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

(def req-id "5022")

(defn handle-event
  [req-id evt]
  (log/info :handle-event/starting {:evt evt})
  (let [{tags     "tags"
         id       "id"
         pow      "pow"
         notified "notified"
         sig      "sig"
         content  "content"} evt
        parsed-content       (json/read-str content)]
    (log/info :parse-message/parsed
              {:evt            evt
               :req-id         req-id
               :tags           tags
               :id             id
               :pow            pow
               :notified       notified
               :sig            sig
               :content        content
               :parsed-content parsed-content})
    {:req-id         req-id
     :tags           tags
     :id             id
     :pow            pow
     :notified       notified
     :sig            sig
     :content        content
     :parsed-content parsed-content}))

(defn handle-eose
  [req-id evt]
  (log/info :handle-eose/starting {:req-id req-id :evt evt})
  nil)

(defn parse-message
  "Parse a response message"
  [message]
  (let [[type req-id evt] message]
    (log/info :parse-message/starting {:req-id req-id :type type})
    (condp = type
      "EVENT" (handle-event req-id evt)
      "EOSE"  (handle-eose req-id evt)
      (do
        (log/warn :parse-message/unknown-type {:type type})
        nil))))

(defn process-messages
  [chan]
  (log/info :process-messages/starting {})
  (async/go
    (let [raw-message (async/<! chan)
          message     (parse-message raw-message)]
      (log/finer :process-messages/finished {:message message})
      message)))

(>defn get-client-for-id
  ([relay-id]
   [::m.n.relays/id => any?]
   (get-client-for-id relay-id true))
  ([relay-id create-if-missing?]
   [::m.n.relays/id boolean? => (? ::client)]
   (if-let [relay (q.n.relays/read-record relay-id)]
     (let [address (::m.n.relays/address relay)]
       (if create-if-missing?
         (let [chan (async/chan)]
           (if-let [client (a.n.relay-client/get-client chan address)]
             client
             (throw (RuntimeException. "Failed to create client"))))
         (if-let [client (a.n.relay-client/get-client-for-address address)]
           client
           (throw (RuntimeException. "Failed to find client")))))
     (throw (RuntimeException. "Failed to find relay")))))

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

;; body is a map that will be turned into a message

(defonce request-counter (atom 0))

(>defn connect!
  "Connect to relay and store connection information"
  [relay-id]
  [::m.n.relays/id => any?]
  (log/info :connect!/starting {:relay-id relay-id})
  (q.n.relays/set-connected relay-id true)
  (let [client   (get-client-for-id relay-id)]
    (log/finer :connect!/finished {:client client})
    client))

(>defn send!
  "Send a message to a relay"
  [relay-id body]
  [::m.n.relays/id any? => ds/channel?]
  (log/info :send!/starting {:relay-id relay-id :body body})
  (if-let [relay (q.n.relays/read-record relay-id)]
    (let [address (::m.n.relays/address relay)
          client     (connect! relay-id)
          chan       (a.n.relay-client/get-channel address)
          request-id (str "adhoc " @request-counter)]
      (swap! request-counter inc)
      (a.n.relay-client/send! client request-id body)
      chan)
    (throw (RuntimeException. "Failed to find relay"))))

(>defn disconnect!
  "Disconnect from relay and clear connection information"
  [relay-id]
  [::m.n.relays/id => any?]
  (log/info :disconnect!/starting {:relay-id relay-id})
  (let [response (q.n.relays/set-connected relay-id false)
        relay    (q.n.relays/read-record relay-id)
        url      (::m.n.relays/address relay)
        client   (get-client-for-id relay-id false)]
    (ws/close! client)
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

(defn register-relay!
  [address]
  (log/info :register-relay/starting {:address address}))

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

  (some->
   (q.n.relays/index-ids)
   first
   q.n.relays/read-record)

  nil)
