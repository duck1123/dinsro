(ns dinsro.components.seed.core-nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::chain string?)
(>def ::network string?)
(>def ::name string?)
(>def ::host string?)
(>def ::port integer?)
(>def ::rpcuser string?)
(>def ::rpcpass string?)
(>def ::peers (s/coll-of string?))

(>def ::item
  (s/keys
   :req-un
   [::name
    ::host
    ::port
    ::rpcuser
    ::rpcpass
    ::chain
    ::network
    ::peers]))
