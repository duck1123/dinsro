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
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/pubkeys.cljc][Joins]]
;; [[../../model/nostr/pubkeys.cljc][Model]]
;; [[../../queries/nostr/pubkeys.clj][Queries]]
;; [[../../ui/nostr/pubkeys.cljs][UI]]

(>defn fetch-pubkey!
  "Fetch info about pubkey from relay"
  ([pubkey]
   [::m.n.pubkeys/pubkey => any?]
   (if-let [relay-id (first (q.n.relays/index-ids))]
     (fetch-pubkey! pubkey relay-id)
     (throw (RuntimeException. "No relays"))))
  ([pubkey relay-id]
   [::m.n.pubkeys/pubkey ::m.n.relays/id => any?]
   (do
     (log/info :fetch-pubkey!/starting {:pubkey pubkey :relay-id relay-id})
     (let [body         {:authors [pubkey] :kinds [0]}
           chan         (a.n.relays/send! relay-id body)
           message-chan (a.n.relays/process-messages chan)
           timeout-chan (a.n.relays/take-timeout message-chan)]
       (async/<!! timeout-chan)))))

(>defn fetch-contact!
  [pubkey-id]
  [::m.n.pubkeys/id => any?]
  (let [pubkey-record (q.n.pubkeys/read-record pubkey-id)
        pubkey        (::m.n.pubkeys/pubkey pubkey-record)]
    (log/info :fetch-contact!/starting {:pubkey pubkey})
    (fetch-pubkey! pubkey)))

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

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))
  (q.n.relays/read-record relay-id)

  a.n.relays/connections

  (a.n.relays/disconnect! relay-id)

  (def pubkey-id (first (q.n.pubkeys/index-ids)))

  (q.n.pubkeys/read-record pubkey-id)

  (fetch-contact! pubkey-id)

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
