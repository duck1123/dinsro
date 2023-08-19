(ns dinsro.notebooks.nostr.runs-notebook
  (:require
   [dinsro.actions.nostr.runs :as a.n.runs]
   [dinsro.queries.nostr.connections :as q.n.connections]
   [dinsro.queries.nostr.runs :as q.n.runs]))

;; [[../../../../main/dinsro/actions/nostr/runs.clj]]
;; [[../../../../main/dinsro/queries/nostr/runs.clj]]

(comment

  (def run-id (first (q.n.runs/find-active)))
  run-id

  (a.n.runs/stop! run-id)

  (q.n.connections/index-ids)
  (def connection-id (first (q.n.connections/find-connected)))
  connection-id

  (q.n.runs/find-by-connection connection-id)
  (q.n.runs/find-active-by-connection connection-id)

  (q.n.runs/find-active)

  (q.n.runs/read-record run-id)

  nil)
