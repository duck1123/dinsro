(ns dinsro.actions.nostr.badge-awards
  (:require
   [dinsro.queries.nostr.badge-awards :as q.n.badge-awards]
   [lambdaisland.glogc :as log]))

;; [[../../queries/nostr/badge_awards.clj]]
;; [[../../responses/nostr/badge_awards.cljc]]

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.n.badge-awards/delete! id))
