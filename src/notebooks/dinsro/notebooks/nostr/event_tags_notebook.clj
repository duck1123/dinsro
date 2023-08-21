(ns dinsro.notebooks.nostr.event-tags-notebook
  (:require
   [dinsro.queries.nostr.event-tags :as q.n.event-tags]
   [dinsro.queries.nostr.events :as q.n.events]))

;; [[../../../../main/dinsro/actions/nostr/event_tags.clj]]

(comment

  (q.n.events/find-by-note-id "e4f5b8f980885e5f013d1b0549ce871c42d892e744da3e4a611a65202a227472")

  (q.n.events/index-ids)
  (q.n.event-tags/index-ids)

  (doseq [event-id (q.n.event-tags/index-ids)]
    (q.n.event-tags/delete! event-id))

  (q.n.events/find-by-note-id "36df49af7fe181520beee31644f121ea2bb8e4ff99468d08f56040e5b792bea5")

  nil)
