(ns dinsro.actions.nostr.pubkeys
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [dinsro.actions.contacts :as a.contacts]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

(defn fetch!
  [pubkey-id]
  (log/info :fetch!/starting {:pubkey-id pubkey-id}))

(defn send-adhoc-request
  [client pubkey]
  (ws/send! client (json/json-str (a.n.relays/adhoc-request [pubkey]))))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))

  (q.n.pubkeys/index-ids)

  (def response (a.n.relays/get-client-for-id relay-id))

  response
  (def chan (:chan response))
  (def client (:client response))
  client

  (send-adhoc-request client a.contacts/duck)
  (send-adhoc-request client a.contacts/matt-odell)

  (ws/send! client (json/json-str (a.n.relays/adhoc-request [a.contacts/duck])))

  (async/<!! (a.n.relays/process-messages chan)))
