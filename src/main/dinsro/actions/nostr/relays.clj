(ns dinsro.actions.nostr.relays
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.contacts :as a.contacts]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [hato.client :as hc]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

(defonce connections (atom {}))

(defn on-message
  [chan]
  (fn [_ws msg _last?]
    (let [msg-str (str msg)
          o       (json/read-str msg-str)]
      (async/put! chan o))))

(defn on-close
  [chan]
  (fn [_ws _status _reason]
    (log/info :on-closed/received {})
    (async/close! chan)))

(def sample-ids
  ["29f63b70d8961835b14062b195fc7d84fa810560b36dde0749e4bc084f0f8952"
   "82341f882b6eabcd2ba7f1ef90aad961cf074af15b9ef44a09f9d2a8fbfbe6a2",
   "247e76e8ec55a48010575785960550971654e12a4c8e1980b84432b8e538c485",
   "d12652d77742fee5e27f0d37ec034c20ec87923589537924db0174f0dc7ee132",
   "3bf0c63fcb93463407af97a5e5ee64fa883d107ef9e558472c4eb9aaaefa459d",
   a.contacts/duck
   a.contacts/matt-odell])

(def req-id "5022")

(defn adhoc-request
  [author-ids]
  (let [id req-id]
    ["REQ" (str "adhoc " id)
     {:authors author-ids
      :kinds   [0]}]))

(def ws-url "wss://relay.kronkltd.net")

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
  [address]
  [string? => any?]
  (if-let [item (get @connections address)]
    (:chan item)
    (throw (RuntimeException. "No channel"))))

(defn get-client-for-id
  [relay-id]
  (let [chan                          (async/chan)
        relay                         (q.n.relays/read-record relay-id)
        {::m.n.relays/keys [address]} relay
        client (get-client chan address)]
    client))

(defn parse-message
  "Parse a response message"
  [message]
  (let [[type req-id evt]    message
        {tags     "tags"
         id       "id"
         pow      "pow"
         notified "notified"
         sig      "sig"
         content  "content"} evt]
    (log/info :parse-message/parsed
              {:evt      evt
               :type     type
               :req-id   req-id
               :tags     tags
               :id       id
               :pow      pow
               :notified notified
               :sig      sig
               :content  content})
    {:type     type
     :req-id   req-id
     :tags     tags
     :id       id
     :pow      pow
     :notified notified
     :sig      sig
     :content  content}))

(defn process-messages
  [chan]
  (async/go
    (let [message (parse-message (async/<! chan))]
      message)))

(defn take-timeout
  [chan]
  (async/go
    (let [[v c] (async/alts! [chan (async/timeout 3000)])]
      (if (= c chan)
        v
        (do
          (comment (async/close! chan))
          :timeout)))))

(comment

  (hc/get "https://relay.kronkltd.net")

  (json/json-str
   (adhoc-request sample-ids))

  (def chan (async/chan))

  (def client (get-client chan ws-url))

  client

  (ws/close! client)

  (ws/send! client (json/json-str (adhoc-request sample-ids)))

  chan

  (process-messages chan)

  (some->
   (q.n.relays/index-ids)
   first
   q.n.relays/read-record)

  (async/<!! chan)

  (def content "")

  nil)