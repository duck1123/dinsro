(ns dinsro.options.core.transactions
  (:refer-clojure :exclude [hash sequence time])
  (:require
   [dinsro.model.core.transactions :as m.c.transactions]))

(def id ::m.c.transactions/id)

(def tx-id ::m.c.transactions/tx-id)

(def block-hash ::m.c.transactions/block-hash)

(def block-time ::m.c.transactions/block-time)

(def confirmations ::m.c.transactions/confirmations)

(def hash ::m.c.transactions/hash)

(def hex ::m.c.transactions/hex)

(def lock-time ::m.c.transactions/lock-time)

(def size ::m.c.transactions/size)

(def time ::m.c.transactions/time)

(def version ::m.c.transactions/version)

(def block ::m.c.transactions/block)

(def fetched? ::m.c.transactions/fetched?)

(def node ::m.c.transactions/node)