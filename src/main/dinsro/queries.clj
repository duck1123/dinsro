(ns dinsro.queries
  (:require
   [dinsro.queries.nostr :as q.nostr]
   [lambdaisland.glogc :as log]))

(defn initialize-queries!
  []
  (log/info :initialize-queries!/starting {})
  (q.nostr/initialize-queries!)
  (log/info :initialize-queries!/finished {}))
