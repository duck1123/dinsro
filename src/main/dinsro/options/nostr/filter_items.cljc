(ns dinsro.options.nostr.filter-items
  (:refer-clojure :exclude [filter type])
  (:require
   [dinsro.model.nostr.filter-items :as m.n.filter-items]))

(def id ::m.n.filter-items/id)
(def filter ::m.n.filter-items/filter)
(def type ::m.n.filter-items/type)
(def index ::m.n.filter-items/index)
(def kind ::m.n.filter-items/kind)
(def event ::m.n.filter-items/event)
(def pubkey ::m.n.filter-items/pubkey)