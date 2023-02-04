(ns dinsro.actions.nostr.pubkey-contacts
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/pubkey_contacts.cljc][Pubkey Contacts Model]]
;; [[../../mutations/nostr/pubkey_contacts.cljc][Pubkey Contact Mutations]]

(>defn fetch-contacts!
  ([pubkey-id]
   [::m.n.pubkeys/id => any?]
   (let [relay-id (first (q.n.relays/index-ids))]
     (fetch-contacts! pubkey-id relay-id)))
  ([pubkey-id relay-id]
   [::m.n.pubkeys/id ::m.n.relays/id => any?]
   (do
     (log/info :fetch-contacts!/starting {:pubkey-id pubkey-id :relay-id relay-id})
     (if-let [pubkey (q.n.pubkeys/read-record pubkey-id)]
       (let [pubkey-hex (::m.n.pubkeys/hex pubkey)
             body       {:authors [pubkey-hex] :kinds [3]}
             ch   (a.n.relays/send! relay-id body)]
         (async/go-loop []
           (let [msg (async/<! ch)]
             (log/info :fetch-contacts!/received {:msg msg})
             (recur))))
       (throw (RuntimeException. "Failed to find pubkey"))))))

(>defn do-fetch-contacts!
  [ident]
  [::m.n.pubkeys/ident => any?]
  (log/info :do-fetch-contacts!/starting {:ident ident})
  (if-let [pubkey-id (::m.n.pubkeys/id ident)]
    (let [response (fetch-contacts! pubkey-id)]
      (log/info :do-fetch-contacts!/finished {:response response})
      response)
    (throw (RuntimeException. "Failed to find pubkey"))))
