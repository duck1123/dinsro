(ns dinsro.processors.nostr.relays
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.connections :as a.n.connections]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.specs.nostr.relays :as s.n.relays]
   [lambdaisland.glogc :as log]))

(defn delete!
  [props]
  (log/info :delete!/starting {:props props})
  (mu/error-response "Not Implemented"))

(defn submit!
  [props]
  (log/info :submit!/starting {:props props})
  (let [address  (::m.n.relays/address props)
        relay-id (a.n.relays/register-relay! address)]
    {::mu/status       :ok
     ::m.n.relays/item (q.n.relays/read-record relay-id)}))

(defn fetch-events!
  [props]
  (log/info :fetch-events!/starting {:props props})
  (let [{relay-id  ::m.n.relays/id
         pubkey-id ::m.n.pubkeys/id} props]
    (log/info :fetch-events!/starting {:relay-id  relay-id
                                       :pubkey-id pubkey-id})))

(>defn connect!
  [{::m.n.relays/keys [id]}]
  [::s.n.relays/connect!-request => ::s.n.relays/connect!-response]
  (log/info :connect!/started {:id id})
  (a.n.connections/register-connection! id)
  (a.n.relays/connect! id)
  (let [updated-node (q.n.relays/read-record id)]
    {::mu/status       :ok
     ::m.n.relays/item updated-node}))
