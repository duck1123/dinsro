(ns dinsro.queries
  (:require
   [dinsro.queries.instances :as q.instances]
   [dinsro.queries.nostr :as q.nostr]
   [lambdaisland.glogc :as log]))

(defn initialize-queries!
  []
  (log/info :initialize-queries!/starting {})
  (q.instances/initialize-queries!)
  (q.nostr/initialize-queries!)
  (log/info :initialize-queries!/finished {}))
