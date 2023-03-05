(ns dinsro.actions.nostr.event-tags
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.queries.nostr.event-tags :as q.n.event-tags]
   [lambdaisland.glogc :as log]))

;; [[../../queries/nostr/event_tags.clj][Event Tag Queries]]

(>defn register-tag!
  [event-id tag idx]
  [::m.n.events/id ::m.n.event-tags/params number? => any?]
  (log/info :register-tag!/start {:event-id event-id :tag tag})
  (let [[key value extra] tag]
    (condp = key
      "p"
      (q.n.event-tags/create-record
       {::m.n.event-tags/idx    idx
        ::m.n.event-tags/parent event-id
        ::m.n.event-tags/pubkey value
        ::m.n.event-tags/extra  extra})

      "e"
      (q.n.event-tags/create-record
       {::m.n.event-tags/idx    idx
        ::m.n.event-tags/parent event-id
        ::m.n.event-tags/event  value
        ::m.n.event-tags/extra  extra})

      (throw (RuntimeException. "unknown key")))))

(comment

  (q.n.event-tags/index-ids)

  nil)
