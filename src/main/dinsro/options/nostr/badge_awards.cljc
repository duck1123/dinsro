(ns dinsro.options.nostr.badge-awards
  (:require
   [dinsro.model.nostr.badge-awards :as m.n.badge-awards]))

(def id
  "The id of a badge award"
  ::m.n.badge-awards/id)

(def pubkey
  "The pubkey that created this award"
  ::m.n.badge-awards/pubkey)
