(ns dinsro.actions.nostr.badge-acceptances
  (:require
   [dinsro.queries.nostr.badge-acceptances :as q.n.badge-acceptances]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/badge_acceptances.cljc]]
;; [[../../model/nostr/badge_acceptances.cljc]]
;; [[../../queries/nostr/badge_acceptances.clj]]
;; [[../../processors/nostr/badge_acceptances.clj]]

(defn fetch!
  [relay-id pubkey-id]
  (log/info :fetch!/starting {:relay-id relay-id :pubkey-id pubkey-id}))

(defn delete!
  [id]
  (log/info :delete!/starting {:id id})
  (q.n.badge-acceptances/delete! id))
