(ns dinsro.actions.nostr.events
  (:require
   [dinsro.queries.nostr.events :as q.n.events]))

;; [[../../model/nostr/events.cljc][Event Model]]


(comment

  (map q.n.events/read-record (q.n.events/index-ids))

  nil)
