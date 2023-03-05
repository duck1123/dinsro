(ns dinsro.actions.nostr.events
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/events.cljc][Event Model]]
;; [[../../queries/nostr/events.clj][Event Queries]]

(>defn fetch-events!
  [pubkey-id]
  [::m.n.pubkeys/id => any?]
  (log/info :fetch-events!/starting {:pubkey-id pubkey-id}))

(>defn fetch-event!
  [event-id]
  [::m.n.events/id => any?]
  (log/info :fetch-event!/starting {:event-id event-id})
  (if-let [event (q.n.events/read-record event-id)]
    (do
      (log/info :fetch-event!/fetched {:event event})
      (let [relay-ids (q.n.events/index-ids)]
        (doseq [relay-id relay-ids]
          (log/info :fetch-event!/relay {:relay-id relay-id})
          (let [note-id ""
                body    {:kinds [0] :ids [note-id]}]
           (a.n.relays/send! relay-id body)))))
    (throw (RuntimeException. "Failed to find event"))))

(defn do-fetch!
  [props]
  (log/info :do-fetch!/starting {:props props})
  (let [event-id (::m.n.events/id props)]
    (fetch-event! event-id)))

(comment

  (q.n.pubkeys/index-ids)

  (def alice-id (first (q.n.pubkeys/find-by-name "alice")))
  (def duck-id (first (q.n.pubkeys/find-by-name "duck")))

  (q.n.pubkeys/read-record alice-id)
  (q.n.pubkeys/read-record duck-id)

  (q.n.events/find-by-author duck-id)
  (q.n.events/find-by-author alice-id)

  (map q.n.events/read-record (q.n.events/index-ids))

  nil)
