(ns dinsro.actions.nostr.event-tags
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.queries.nostr.event-tags :as q.n.event-tags]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [lambdaisland.glogc :as log]))

;; [[../../queries/nostr/event_tags.clj][Event Tag Queries]]

(>defn register-tag!
  [event-id tag idx]
  [::m.n.events/id any? number? => any?]
  (log/info :register-tag!/start {:event-id event-id :tag tag})
  (let [[key value extra] tag]
    (condp = key
      "p"
      (if-let [pubkey-id (q.n.pubkeys/find-by-hex value)]
        (q.n.event-tags/create-record
         {::m.n.event-tags/idx    idx
          ::m.n.event-tags/parent event-id
          ::m.n.event-tags/pubkey pubkey-id
          ::m.n.event-tags/extra  extra})
        (throw (RuntimeException. "Failed to find pubkey")))

      "e"
      (if-let [target-id (q.n.events/find-by-note-id value)]
        (q.n.event-tags/create-record
         {::m.n.event-tags/idx    idx
          ::m.n.event-tags/parent event-id
          ::m.n.event-tags/event  target-id
          ::m.n.event-tags/extra  extra})
        (throw (RuntimeException "Failed to find note")))

      (throw (RuntimeException. "unknown key")))))

(comment

  (q.n.events/find-by-note-id "e4f5b8f980885e5f013d1b0549ce871c42d892e744da3e4a611a65202a227472")
  (q.n.event-tags/index-ids)

  nil)
