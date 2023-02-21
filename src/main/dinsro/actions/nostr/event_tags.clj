(ns dinsro.actions.nostr.event-tags
  (:require
   [dinsro.queries.nostr.event-tags :as q.n.event-tags]
   [lambdaisland.glogc :as log]))

;; [[../../queries/nostr/event_tags.clj][Event Tag Queries]]

(defn register-tag!
  [event-id tag]
  (log/info :register-tag!/start {:event-id event-id :tag tag}))

(comment

  (q.n.event-tags/index-ids)

  nil)
