(ns dinsro.processors.nostr.badge-acceptances
  (:require
   [dinsro.actions.nostr.badge-acceptances :as a.n.badge-acceptances]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]))

;; [../../actions/nostr/badge_acceptances.clj]
;; [../../model/nostr/badge_acceptances.cljc]

(defn fetch!
  [props]
  (let [{relay-id ::m.n.relays/id
         pubkey-id ::m.n.pubkeys/id} props]
    (a.n.badge-acceptances/fetch! relay-id pubkey-id)
    {::mu/status :ok}))
