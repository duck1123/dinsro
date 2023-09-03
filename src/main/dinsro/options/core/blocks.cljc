(ns dinsro.options.core.blocks
  (:refer-clojure :exclude [hash time])
  (:require
   [dinsro.model.core.blocks :as m.c.blocks]))

(def id ::m.c.blocks/id)

(def hash ::m.c.blocks/hash)

(def height ::m.c.blocks/height)

(def merkle-root ::m.c.blocks/merkle-root)

(def nonce ::m.c.blocks/nonce)

(def next-block ::m.c.blocks/next-block)

(def previous-block ::m.c.blocks/previous-block)

(def time ::m.c.blocks/time)

(def size ::m.c.blocks/size)

(def bits ::m.c.blocks/bits)

(def chainwork ::m.c.blocks/chainwork)

(def difficulty ::m.c.blocks/difficulty)

(def weight ::m.c.blocks/weight)

(def version ::m.c.blocks/version)

(def version-hex ::m.c.blocks/version-hex)

(def transaction-count ::m.c.blocks/transaction-count)

(def stripped-size ::m.c.blocks/stripped-size)

(def median-time ::m.c.blocks/median-time)

(def fetched? ::m.c.blocks/fetched?)

(def network ::m.c.blocks/network)

(def transactions ::m.c.blocks/transactions)

(def confirmations ::m.c.blocks/confirmations)

