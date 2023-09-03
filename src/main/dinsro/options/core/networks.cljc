(ns dinsro.options.core.networks
  (:refer-clojure :exclude [name])
  (:require
   [dinsro.model.core.networks :as m.c.networks]))

(def id ::m.c.networks/id)

(def name ::m.c.networks/name)
(def chain ::m.c.networks/chain)