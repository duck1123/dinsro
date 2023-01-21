(ns dinsro.actions.nostr.pubkeys
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.contacts :as a.contacts]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.specs :as ds]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/pubkeys.cljc][Joins]]
;; [[../../model/nostr/pubkeys.cljc][Model]]
;; [[../../queries/nostr/pubkeys.clj][Queries]]
;; [[../../ui/nostr/pubkeys.cljs][UI]]

(>defn fetch-pubkey!
  "Fetch info about pubkey from relay"
  ([pubkey]
   [::m.n.pubkeys/pubkey => ds/channel?]
   (if-let [relay-id (first (q.n.relays/index-ids))]
     (fetch-pubkey! pubkey relay-id)
     (throw (RuntimeException. "No relays"))))
  ([pubkey relay-id]
   [::m.n.pubkeys/pubkey ::m.n.relays/id => ds/channel?]
   (do
     (log/info :fetch-pubkey!/starting {:pubkey pubkey :relay-id relay-id})
     (async/go
       (let [body    {:authors [pubkey] :kinds [0]}
             chan    (a.n.relays/send! relay-id body)
             message (async/<! (a.n.relays/process-messages chan))]
         (log/info :fetch-pubkey!/fetched {:message message})
         message)))))

(>defn send-adhoc-request
  [client pubkey]
  [any? ::m.n.pubkeys/pubkey => any?]
  (let [msg (json/json-str (a.n.relays/adhoc-request [pubkey]))]
    (ws/send! client msg)))

(>defn poll!
  [relay-id]
  [::m.n.relays/id => any?]
  (let [relay   (q.n.relays/read-record relay-id)
        address (::m.n.relays/address relay)
        chan    (a.n.relays/get-channel address)]
    (async/<!! (a.n.relays/take-timeout (a.n.relays/process-messages chan)))))

(>defn parse-content
  [content]
  [string? => any?]
  (log/info :parse-content/starting {:content content})
  (let [data (json/read-str content)]
    (log/info :parse-content/parsed {:data data})
    (let [{name    "name"
           about   "about"
           nip05   "nip05"
           lud06   "lud06"
           lud16   "lud16"
           picture "picture"
           website "website"} data]
      {::m.n.pubkeys/name    name
       ::m.n.pubkeys/about   about
       ::m.n.pubkeys/nip05   nip05
       ::m.n.pubkeys/lud06   lud06
       ::m.n.pubkeys/lud16   lud16
       ::m.n.pubkeys/picture picture
       ::m.n.pubkeys/website website})))

(>defn update-pubkey!
  [pubkey-id]
  [::m.n.pubkeys/id => any?]
  (log/info :update-pubkey!/starting {:pubkey-id pubkey-id})
  (async/go
    (if-let [pubkey (q.n.pubkeys/read-record pubkey-id)]
      (let [hex      (::m.n.pubkeys/pubkey pubkey)
            response (async/<! (fetch-pubkey! hex))]
        (log/info :update-pubkey!/finished {:response response})
        response)
      (throw (RuntimeException. "No pubkey")))))

(>defn fetch-contact!
  [pubkey-id]
  [::m.n.pubkeys/id => ds/channel?]
  (update-pubkey! pubkey-id))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))
  (q.n.relays/read-record relay-id)

  (def duck-content "{\"name\":\"dinsro\",\"about\":\"sats-first budget management\\n\\nhttps://github.com/duck1123/dinsro\",\"nip05\":\"dinsro@detbtc.com\",\"lud06\":\"lnurl1dp68gurn8ghj7cm0d9hx7uewd9hj7tnhv4kxctttdehhwm30d3h82unvwqhkgatrdvrwrevc\",\"lud16\":\"duck@coinos.io\",\"picture\":\"https://void.cat/d/JpoHXq8TQNpB7H6oCpTz6J\",\"website\":\"https://dinsro.com/\"}")

  a.n.relays/connections

  (a.n.relays/disconnect! relay-id)

  (def pubkey-id (first (q.n.pubkeys/index-ids)))
  pubkey-id

  (q.n.pubkeys/read-record pubkey-id)

  (def contact (fetch-contact! pubkey-id))

  contact

  (poll! relay-id)

  (def response (a.n.relays/get-client-for-id relay-id))

  response
  (def chan (:chan response))
  (def client (:client response))
  client

  (send-adhoc-request client a.contacts/duck)
  (send-adhoc-request client a.contacts/matt-odell)

  (ws/send! client (json/json-str (a.n.relays/adhoc-request [a.contacts/duck])))

  (async/poll! (a.n.relays/process-messages chan))

  (fetch-pubkey! a.contacts/duck)

  (async/<!! (a.n.relays/take-timeout (a.n.relays/process-messages chan)))

  chan

  a.n.relays/connections

  nil)
