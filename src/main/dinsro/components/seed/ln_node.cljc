(ns dinsro.components.seed.ln-node
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::name string?)
(>def ::host string?)
(>def ::fileserver-host string?)
(>def ::port string?)
(>def ::node string?)
(>def ::mnemonic (s/coll-of string?))
(>def ::peers any?)
(>def ::txes any?)

(>def ::item
  (s/keys
   :req-un
   [::name
    ::host
    ::fileserver-host
    ::port
    ::node
    ::mnemonic
    ::peers
    ::txes]))
(def item ::item)
