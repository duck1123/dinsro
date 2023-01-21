(ns dinsro.queries.nostr
  (:require
   [dinsro.queries.nostr.relays :as q.n.relays]
   [lambdaisland.glogc :as log]))

;; [[../queries.clj][Queries]]

(defn initialize-queries!
  []
  (log/info :initialize-queries!/starting {})
  (q.n.relays/initialize-queries!)
  (log/info :initialize-queries!/finished {}))
