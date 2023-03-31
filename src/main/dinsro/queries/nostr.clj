(ns dinsro.queries.nostr
  (:require
   [dinsro.queries.nostr.connections :as q.n.connections]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.queries.nostr.runs :as q.n.runs]
   [lambdaisland.glogc :as log]))

;; [[../queries.clj][Queries]]

(defn initialize-queries!
  []
  (log/info :initialize-queries!/starting {})
  (q.n.connections/initialize-queries!)
  (q.n.relays/initialize-queries!)
  (q.n.requests/initialize-queries!)
  (q.n.runs/initialize-queries!)
  (log/info :initialize-queries!/finished {}))
