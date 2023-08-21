(ns dinsro.notebooks.nostr.requests-notebook
  (:require
   [dinsro.actions.nostr.requests :as a.n.requests]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.specs :as ds]))

;; # Requests

;; [[../../../../main/dinsro/model/nostr/requests.cljc]]

(comment

  (q.n.relays/index-ids)

  (def relay-id (q.n.relays/find-by-address "wss://nostr-pub.wellorder.net"))
  relay-id
  (def code "adhoc 0")

  (map q.n.requests/read-record (q.n.requests/find-by-relay relay-id))

  (q.n.requests/find-by-relay relay-id)
  (q.n.requests/find-by-code code)
  (q.n.requests/find-by-relay-and-code relay-id code)

  (ds/gen-key ::m.n.requests/item)

  (def request-id (first (q.n.requests/index-ids)))
  request-id
  (q.n.requests/read-record request-id)

  (q.n.requests/index-ids)

  (a.n.requests/get-query-string request-id)

  (q.n.relays/read-record (q.n.requests/find-relay request-id))

  (q.n.requests/delete-all!)

  (q.n.requests/read-record (first (q.n.requests/index-ids)))

  (some-> relay-id q.n.requests/find-relay q.n.relays/read-record)

  ds/date

  (ds/->inst)

  nil)
