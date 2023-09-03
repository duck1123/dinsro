(ns dinsro.options.nostr.event-tags
  (:refer-clojure :exclude [type])
  (:require
   [dinsro.model.nostr.event-tags :as m.n.event-tags]))

(def id ::m.n.event-tags/id)
(def index ::m.n.event-tags/index)
(def parent ::m.n.event-tags/parent)
(def type ::m.n.event-tags/type)
(def raw-value ::m.n.event-tags/raw-value)
(def extra ::m.n.event-tags/extra)
(def event ::m.n.event-tags/event)

(def pubkey ::m.n.event-tags/pubkey)
