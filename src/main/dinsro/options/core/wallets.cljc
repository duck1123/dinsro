(ns dinsro.options.core.wallets
  (:refer-clojure :exclude [key name])
  (:require
   [dinsro.model.core.wallets :as m.c.wallets]))

(def id ::m.c.wallets/id)

(def name ::m.c.wallets/name)

(def derivation ::m.c.wallets/derivation)

(def key ::m.c.wallets/key)

(def ext-public-key ::m.c.wallets/ext-public-key)

(def ext-private-key ::m.c.wallets/ext-private-key)

(def mnemonic ::m.c.wallets/mnemonic)

(def network ::m.c.wallets/network)

(def user ::m.c.wallets/user)