(ns dinsro.processors.nostr.relays
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.connections :as a.n.connections]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.responses.nostr.relays :as r.n.relays]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/relays.clj]]

(def model-key ::m.n.relays/id)

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.relays/delete! id)
    {::mu/status                   :ok
     ::r.n.relays/deleted-records (m.n.relays/idents [id])}))

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
  [::r.n.relays/connect!-request => ::r.n.relays/connect!-response]
  (log/info :connect!/started {:id id})
  (a.n.connections/register-connection! id)
  (a.n.relays/connect! id)
  (let [updated-node (q.n.relays/read-record id)]
    {::mu/status       :ok
     ::m.n.relays/item updated-node}))
