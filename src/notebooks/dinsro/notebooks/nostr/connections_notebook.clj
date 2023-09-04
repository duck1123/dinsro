(ns dinsro.notebooks.nostr.connections-notebook
  (:require
   [dinsro.actions.nostr.connections :as a.n.connections]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.options.nostr.connections :as o.n.connections]
   [dinsro.queries.nostr.connections :as q.n.connections]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.queries.nostr.runs :as q.n.runs]
   [dinsro.specs :as ds]))

;; Connections Notebook

;; [[../../../../main/dinsro/actions/nostr/connections.clj]]

@a.n.connections/connections

(comment

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id

  (q.n.connections/find-by-relay relay-id)

  (ds/gen-key ::m.n.connections/item)
  (q.n.connections/index-ids)

  (a.n.connections/register-connection! relay-id)

  (map q.n.connections/read-record (q.n.connections/find-connected))

  (def connection-id (first (q.n.connections/find-connected)))
  connection-id

  (q.n.connections/set-disconnected! connection-id)

  (q.n.connections/read-record connection-id)
  (a.n.connections/disconnect! connection-id)

  (some-> (q.n.relays/index-ids {o.n.connections/id connection-id})
          first
          q.n.relays/read-record)

  (a.n.connections/start! connection-id)

  (a.n.connections/get-client connection-id)

  (def code (::m.n.requests/code (q.n.requests/read-record (first (q.n.requests/index-ids)))))

  (q.n.runs/index-ids)
  (q.n.runs/find-active)

  (q.n.runs/find-active-by-connection connection-id)
  (q.n.runs/find-active-by-code code)

  (q.n.runs/find-active-by-connection-and-code connection-id code)

  (q.n.connections/delete! connection-id)
  (q.n.connections/read-record connection-id)

  (q.n.connections/set-connecting! connection-id)
  (q.n.connections/set-connected! connection-id)
  (q.n.connections/set-disconnected! connection-id)
  (q.n.connections/set-errored! connection-id)

  nil)
