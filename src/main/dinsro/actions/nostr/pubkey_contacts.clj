(ns dinsro.actions.nostr.pubkey-contacts
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [lambdaisland.glogc :as log]))

(>defn fetch-contacts!
  [pubkey-id]
  [::m.n.pubkeys/id => any?]
  (log/info :fetch-contacts!/starting {:pubkey-id pubkey-id}))

(>defn do-fetch-contacts!
  [ident]
  [::m.n.pubkeys/ident => any?]
  (log/info :do-fetch-contacts!/starting {:ident ident})
  (if-let [pubkey-id (::m.n.pubkeys/id ident)]
    (let [response (fetch-contacts! pubkey-id)]
      (log/info :do-fetch-contacts!/finished {:response response})
      response)
    (throw (RuntimeException. "Failed to find pubkey"))))
