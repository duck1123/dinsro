(ns dinsro.actions.nostr.pubkeys
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [dinsro.actions.contacts :as a.contacts]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

(defn fetch-pubkey!
  [pubkey]
  (log/info :fetch-pubkey!/starting {:pubkey pubkey})
  (let [relay-id (first (q.n.relays/index-ids))
        relay    (q.n.relays/read-record relay-id)
        address  (::m.n.relays/address relay)
        client   (a.n.relays/get-client-for-id relay-id)
        chan     (a.n.relays/get-channel address)
        request  (a.n.relays/adhoc-request [pubkey])
        body     {:authors [pubkey] :kinds [0]}
        message  (json/json-str request)]
    (a.n.relays/send! relay-id body)
    (async/<!! (a.n.relays/take-timeout (a.n.relays/process-messages chan)))))

(defn fetch-contact!
  [pubkey-id]
  (let [pubkey-record (q.n.pubkeys/read-record pubkey-id)
        pubkey        (::m.n.pubkeys/pubkey pubkey-record)]
    (log/info :fetch-contact!/starting {:pubkey pubkey})
    (fetch-pubkey! pubkey)))

(defn send-adhoc-request
  [client pubkey]
  (ws/send! client (json/json-str (a.n.relays/adhoc-request [pubkey]))))

(defn poll!
  [relay-id]
  (let [relay   (q.n.relays/read-record relay-id)
        address (::m.n.relays/address relay)
        chan    (a.n.relays/get-channel address)]
    (async/<!! (a.n.relays/take-timeout (a.n.relays/process-messages chan)))))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))

  (fetch-contact! (first (q.n.pubkeys/index-ids)))

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
