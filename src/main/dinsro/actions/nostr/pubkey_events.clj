(ns dinsro.actions.nostr.pubkey-events
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.nostr.pubkey-contacts :as a.n.pubkey-contacts]
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [lambdaisland.glogc :as log]))

(>defn fetch-events!
  ([pubkey-id]
   [::m.n.pubkeys/id => any?]
   (let [relay-id (first (q.n.relays/index-ids))]
     (fetch-events! pubkey-id relay-id)))
  ([pubkey-id relay-id]
   [::m.n.pubkeys/id ::m.n.relays/id  => any?]
   (do
     (log/info :fetch-events!/starting {:pubkey-id pubkey-id :relay-id relay-id})
     (if-let [pubkey (q.n.pubkeys/read-record pubkey-id)]
       (let [pubkey-hex (::m.n.pubkeys/hex pubkey)
             body       {:authors [pubkey-hex] :kinds [1]}
             ch         (a.n.relays/send! relay-id body)]
         (log/info :fetch-events!/sent {:ch ch})
         (async/go-loop []
           (log/info :fetch-events!/looping {:ch ch})
           (when-let [msg (async/<! ch)]
             (log/info :fetch-events!/received {:msg msg})
             (let [tags (:tags msg)]
               (doseq [tag tags]
                 (log/info :fetch-contacts!/tag {:tag tag})
                 (let [[_p hex _relay] tag
                       target-id       (a.n.pubkeys/register-pubkey! hex)]
                   (a.n.pubkey-contacts/register-contact! pubkey-id target-id))))
             (recur)))

         ch)
       (throw (RuntimeException. "Failed to find pubkey"))))))

(>defn do-fetch!
  [ident]
  [::m.n.pubkeys/ident => any?]
  (log/info :do-fetch!/starting {:ident ident})
  (if-let [pubkey-id (::m.n.pubkeys/id ident)]
    (let [response (fetch-events! pubkey-id)]
      (log/info :do-fetch-contacts!/finished {:response response})
      {:status "ok"})
    (throw (RuntimeException. "Failed to find pubkey"))))
