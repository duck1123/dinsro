(ns dinsro.options.nostr.pubkeys
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]))

;; [[../../model/nostr/pubkeys.cljc]]

(def id
  "The record id for a pubkey"
  ::m.n.pubkeys/id)

(def display-name ::m.n.pubkeys/display-name)

(def hex ::m.n.pubkeys/hex)

(def name ::m.n.pubkeys/name)

(def picture ::m.n.pubkeys/picture)

(def about ::m.n.pubkeys/about)

(def nip05 ::m.n.pubkeys/nip05)

(def website ::m.n.pubkeys/website)

(def lud06 ::m.n.pubkeys/lud06)

(def banner ::m.n.pubkeys/banner)
