(ns dinsro.actions.nostr.badge-definitions
  (:require
   [dinsro.queries.nostr.badge-definitions :as q.n.badge-definitions]
   [lambdaisland.glogc :as log]))

;; [[../../queries/nostr/badge_definitions.clj]]

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.n.badge-definitions/delete! id))
