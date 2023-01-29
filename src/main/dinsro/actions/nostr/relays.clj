(ns dinsro.actions.nostr.relays
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.actions.nostr.subscriptions :as a.n.subscriptions]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.subscriptions :as q.n.subscriptions]
   [dinsro.specs :as ds]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/relays.cljc][Joins]]
;; [[../../model/nostr/relays.cljc][Model]]
;; [[../../queries/nostr/relays.clj][Queries]]
;; [[../../ui/nostr/relays.cljs][UI]]

(>def ::client any?)

(def req-id "5022")

(defonce connections (atom {}))

(>defn handle-message
  [chan _ws msg _last?]
  [ds/channel? any? any? any? => any?]
  (let [msg-str (str msg)
        o       (json/read-str msg-str)]
    (log/debug :handle-message/received {:o o})
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

(>defn adhoc-request
  [author-ids]
  [(s/coll-of string?) => any?]
  (let [id req-id]
    ["REQ" (str "adhoc " id)
     {:authors author-ids
      :kinds   [0]}]))

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

(>defn get-client-for-id
  ([relay-id]
   [::m.n.relays/id => any?]
   (get-client-for-id relay-id true))
  ([relay-id create-if-missing?]
   [::m.n.relays/id boolean? => (? ::client)]
   (let [relay                         (q.n.relays/read-record relay-id)
         {::m.n.relays/keys [address]} relay]
     (if create-if-missing?
       (let [chan   (async/chan)
             client (get-client chan address)]
         client)
       (if-let [client (get-in @connections [address :client])]
         (do
           (log/info :get-client-for-id/cached {:client client})
           client)
         (do
           (log/info :get-client-for-id/missing {})
           nil))))))

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
  (log/info :parse-message/starting {:message message})
  (let [[type req-id evt] message]
    (condp = type
      "EVENT" (handle-event req-id evt)
      "EOSE"  (handle-eose req-id evt)
      (do
        (log/warn :parse-message/unknown-type {:type type})
        #_(throw (RuntimeException. "Unknown type"))
        nil))))

(defn process-messages
  [chan]
  (async/go
    (let [message (parse-message (async/<! chan))]
      (log/info :process-messages/finished {:message message})
      message)))

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

(>defn send!
  "Send a message to a relay"
  [relay-id body]
  [::m.n.relays/id any? => ds/channel?]
  (let [relay      (q.n.relays/read-record relay-id)
        address    (::m.n.relays/address relay)
        client     (get-client-for-id relay-id)
        chan       (get-channel address)
        request-id "adhoc1"
        message    (json/json-str ["REQ" request-id body])]
    (ws/send! client message)
    chan))

(>defn connect!
  "Connect to relay and store connection information"
  [relay-id]
  [::m.n.relays/id => any?]
  (log/info :connect!/starting {:relay-id relay-id})
  (let [response (q.n.relays/set-connected relay-id true)]
    (log/info :connect!/finished {:response response})

    ;; initialize client
    (let [client (get-client-for-id relay-id)]
      (log/info :connect!/got-client {:client client}))

    response))

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
    (swap! connections dissoc url)
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

(>defn do-fetch!
  "Handler for fetch! mutation"
  [{relay-id ::m.n.relays/id}]
  [::m.n.relays/ident => ::m.n.relays/item]
  (log/info :do-fetch!/starting {:relay-id relay-id})
  (let [updated-node (q.n.relays/read-record relay-id)]
    (connect! relay-id)
    (let [subscription-ids (q.n.subscriptions/index-ids)]
      (doseq [subscription-id subscription-ids]
        (log/info :do-fetch!/processing-subscription {:subscription-id subscription-id})
        (if-let [subscription (q.n.subscriptions/read-record subscription-id)]
          (do
            (log/info :do-fetch!/subscription-read {:subscription subscription})
            (a.n.subscriptions/fetch! subscription-id))
          (throw (RuntimeException. "Failed to find subscription"))))
      updated-node)))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))

  (q.n.relays/create-connected-toggle)

  (q.n.relays/initialize-queries!)

  (q.n.relays/read-record relay-id)

  (connect! relay-id)
  (disconnect! relay-id)

  (some->
   (q.n.relays/index-ids)
   first
   q.n.relays/read-record)

  ;; (async/<!! chan)

  ;; (def content "")

  nil)
