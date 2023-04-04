(ns dinsro.actions.nostr.pubkey-events
  (:require
   [clojure.core.async :as async]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.nostr.event-tags :as a.n.event-tags]
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [lambdaisland.glogc :as log]))

(>defn fetch-events!
  ([pubkey-id]
   [::m.n.pubkeys/id => any?]
   (let [channels (map #(fetch-events! pubkey-id %) (q.n.relays/index-ids))]
     (first channels)))
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
             (let [{:keys      [tags id created-at
                                kind sig content]
                    pubkey-hex :pubkey} msg
                   pubkey               (a.n.pubkeys/register-pubkey! pubkey-hex)
                   params               {::m.n.events/note-id    id
                                         ::m.n.events/pubkey     pubkey
                                         ::m.n.events/kind       kind
                                         ::m.n.events/sig        sig
                                         ::m.n.events/content    content
                                         ::m.n.events/created-at created-at}
                   event-id             (q.n.events/register-event! params)]
               (doseq [[idx tag] (map-indexed vector tags)]
                 (log/info :fetch-events!/processing-tag {:tag tag :event-id event-id})
                 (a.n.event-tags/register-tag! event-id tag idx))
               (recur))))
         ch)
       (throw (ex-info "Failed to find pubkey" {}))))))

(>defn do-fetch!
  [ident]
  [::m.n.pubkeys/ident => any?]
  (log/info :do-fetch!/starting {:ident ident})
  (if-let [pubkey-id (::m.n.pubkeys/id ident)]
    (let [response (fetch-events! pubkey-id)]
      (log/info :do-fetch-contacts!/finished {:response response})
      {:status "ok"})
    (throw (ex-info "Failed to find pubkey" {}))))

(defn do-fetch-events!
  [props]
  (log/info :do-fetch-events!/starting {:props props}))
