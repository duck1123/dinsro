(ns dinsro.components.seed.ln-nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [dinsro.components.seed.remote-nodes :as cs.remote-nodes]))

(>def ::name string?)
(>def ::host string?)
(>def ::fileserver-host string?)
(>def ::port string?)
(>def ::node string?)
(>def ::mnemonic (s/coll-of string?))
(>def ::remote-nodes (s/coll-of ::cs.remote-nodes/item))

(>def ::item
  (s/keys
   :req-un
   [::name
    ::host
    ::fileserver-host
    ::port
    ::node
    ::mnemonic
    ::remote-nodes]))
(def item ::item)
