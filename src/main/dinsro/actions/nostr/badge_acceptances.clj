(ns dinsro.actions.nostr.badge-acceptances
  (:require
   [lambdaisland.glogc :as log]))

;; [../../joins/nostr/badge_acceptances.cljc]
;; [../../model/nostr/badge_acceptances.cljc]
;; [../../processors/nostr/badge_acceptances.clj]

(defn fetch!
  [relay-id pubkey-id]
  (log/info :fetch!/starting {:relay-id relay-id :pubkey-id pubkey-id}))
