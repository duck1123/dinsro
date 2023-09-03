(ns dinsro.options.nostr.events
  (:require
   [dinsro.model.nostr.events :as m.n.events]))

(def id ::m.n.events/id)
(def note-id ::m.n.events/note-id)
(def pubkey ::m.n.events/pubkey)

(def created-at ::m.n.events/created-at)
(def kind ::m.n.events/kind)

(def content ::m.n.events/content)

(def sig ::m.n.events/sig)

(def deleted? ::m.n.events/deleted?)