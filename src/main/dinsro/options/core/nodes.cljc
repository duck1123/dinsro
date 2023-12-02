(ns dinsro.options.core.nodes
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.core.nodes :as m.c.nodes]))

(def id ::m.c.nodes/id)

(def host ::m.c.nodes/host)

(def port ::m.c.nodes/port)

(def rpcuser ::m.c.nodes/rpcuser)

(def rpcpass ::m.c.nodes/rpcpass)

(def network ::m.c.nodes/network)

(def name ::m.c.nodes/name)

(def pruned? ::m.c.nodes/pruned?)

(def difficulty ::m.c.nodes/difficulty)

(def size-on-disk ::m.c.nodes/size-on-disk)

(def initial-block-download? ::m.c.nodes/initial-block-download?)

(def best-block-hash ::m.c.nodes/best-block-hash)