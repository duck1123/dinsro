(ns dinsro.actions.nostr.events
  (:require
   [dinsro.queries.nostr.events :as q.n.events]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/events.cljc][Event Model]]


(defn fetch-events!
  [pubkey-id]
  (log/info :fetch-events!/starting {:pubkey-id pubkey-id}))

(comment

  (map q.n.events/read-record (q.n.events/index-ids))

  nil)
