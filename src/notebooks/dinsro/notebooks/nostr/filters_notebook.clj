(ns dinsro.notebooks.nostr.filters-notebook
  (:require
   [dinsro.actions.nostr.filters :as a.n.filters]
   [dinsro.queries.nostr.filters :as q.n.filters]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]))

;; # Filters

(comment

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id

  (def request-id (first (q.n.requests/index-ids)))
  request-id

  (q.n.filters/get-greatest-index request-id)

  (a.n.filters/add-filter! request-id)

  (q.n.filters/index-ids)

  (map q.n.filters/read-record (q.n.filters/index-ids))

  nil)
