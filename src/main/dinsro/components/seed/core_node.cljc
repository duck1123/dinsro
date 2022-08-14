(ns dinsro.components.seed.core-node
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [dinsro.model.core.nodes :as m.c.nodes]))

(>def ::chain string?)
(>def ::network string?)

(>def ::item
  (s/keys
   :req
   [::m.c.nodes/name
    ::m.c.nodes/host
    ::m.c.nodes/port
    ::m.c.nodes/rpcuser
    ::m.c.nodes/rpcpass]
   :req-un
   [::chain
    ::network]))
