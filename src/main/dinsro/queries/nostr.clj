(ns dinsro.queries.nostr
  (:require
   [dinsro.queries.nostr.connections :as q.n.connections]
   [dinsro.queries.nostr.runs :as q.n.runs]
   [lambdaisland.glogc :as log]))

;; [[../queries.clj]]
;; [[../queries/nostr/connections.clj]]
;; [[../queries/nostr/runs.clj]]

(defn initialize-queries!
  []
  (log/info :initialize-queries!/starting {})
  (q.n.connections/initialize-queries!)
  (q.n.runs/initialize-queries!)
  (log/info :initialize-queries!/finished {}))
