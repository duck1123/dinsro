(ns dinsro.actions.nostr.relays
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.relays :as q.n.relays]
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

(defn handle-message
  [chan _ws msg _last?]
  (let [msg-str (str msg)
        o       (json/read-str msg-str)]
    (log/debug :handle-message/received {:o o})
    (async/put! chan o)))

(defn on-message
  "Takes a chan, returns a message handler"
  [chan]
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

(defn get-client
  [chan url]
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
  [string? => any?]
  (if-let [item (get @connections address)]
    (:chan item)
    (throw (RuntimeException. "No channel"))))

(>defn get-client-for-id
  ([relay-id]
   [::m.n.relays/id => any?]
   (get-client-for-id relay-id true))
  ([relay-id create-if-missing]
   [::m.n.relays/id boolean? => (? ::client)]
   (let [relay                         (q.n.relays/read-record relay-id)
         {::m.n.relays/keys [address]} relay]
     (if create-if-missing
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

(defn take-timeout
  [chan]
  (async/go
    (let [[v c] (async/alts! [chan (async/timeout 10000)])]
      (if (= c chan) v (do (comment (async/close! chan)) :timeout)))))

(>defn send!
  [relay-id body]
  [::m.n.relays/id any? => any?]
  (let [relay      (q.n.relays/read-record relay-id)
        address    (::m.n.relays/address relay)
        client     (get-client-for-id relay-id)
        chan       (get-channel address)
        request-id "adhoc1"
        message    (json/json-str ["REQ" request-id body])]
    (ws/send! client message)
    chan))

(>defn connect!
  [relay-id]
  [::m.n.relays/id => any?]
  (log/info :connect!/starting {:relay-id relay-id})
  (let [response (q.n.relays/set-connected relay-id true)]
    (log/info :connect!/finished {:response response})
    (let [client (get-client-for-id relay-id)]
      (log/info :connect!/got-client {:client client})
      response)))

(>defn disconnect!
  [relay-id]
  [::m.n.relays/id => any?]
  (log/info :disconnect!/starting {:relay-id relay-id})
  (let [response (q.n.relays/set-connected relay-id false)
        relay    (q.n.relays/read-record relay-id)
        url      (::m.n.relays/address relay)
        client   (get-client-for-id relay-id)]
    (swap! connections dissoc url)
    (log/info :disconnect!/finished {:response response :client client})
    response))

(>defn toggle-relay!
  [relay]
  [::m.n.relays/item => any?]
  (log/info :toggle-relay!/starting {:relay relay})
  (let [{::m.n.relays/keys [connected]
         relay-id          ::m.n.relays/id} relay]
    (if connected
      (disconnect! relay-id)
      (connect! relay-id))))

(>defn toggle!
  [relay-id]
  [::m.n.relays/id => any?]
  (log/info :toggle!/starting {:relay-id relay-id})
  (let [relay    (q.n.relays/read-record relay-id)
        response (toggle-relay! relay)]
    (log/info :toggle!/finished {:response response})
    response))

(defn do-toggle!
  [props]
  (log/info :do-toggle!/starting {:props props})
  (let [relay-id (::m.n.relays/id props)
        response (toggle! relay-id)]
    (log/info :do-toggle!/finished {:response response})
    (let [relay (q.n.relays/read-record relay-id)]
      {::mu/status       :ok
       ::m.n.relays/item relay})))

(>defn do-fetch!
  "Handler for fetch! mutation"
  [{::m.n.relays/keys [id]}]
  [::m.n.relays/ident => ::m.n.relays/item]
  (log/info :do-fetch!/starting {:id id})
  (let [updated-node (q.n.relays/read-record id)]
    (connect! id)
    updated-node))

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
