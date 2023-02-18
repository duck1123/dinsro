(ns dinsro.actions.nostr.events
  (:require
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/events.cljc][Event Model]]

(defn fetch-events!
  [pubkey-id]
  (log/info :fetch-events!/starting {:pubkey-id pubkey-id}))

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
