(ns dinsro.queries
  (:require
   [dinsro.queries.instances :as q.instances]
   [dinsro.queries.nostr :as q.nostr]
   [lambdaisland.glogc :as log]))

;; [[./model.cljc]]
;; [[./queries/instances.clj]]
;; [[./queries/nostr.clj]]

(defn initialize-queries!
  []
  (log/info :initialize-queries!/starting {})
  (q.instances/initialize-queries!)
  (q.nostr/initialize-queries!)
  (log/info :initialize-queries!/finished {}))
