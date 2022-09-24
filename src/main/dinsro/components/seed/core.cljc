(ns dinsro.components.seed.core
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [dinsro.components.seed.core-node :as cs.core-node]
   [dinsro.components.seed.currencies :as cs.currencies]
   [dinsro.components.seed.networks :as cs.networks]
   [dinsro.components.seed.rate-sources :as cs.rate-sources]
   [dinsro.components.seed.user :as cs.user]))

(>def ::default-chains (s/coll-of string?))
(>def ::default-currencies (s/coll-of ::cs.currencies/item))
(>def ::default-networks cs.networks/item)
(>def ::default-rate-sources (s/coll-of ::cs.rate-sources/item))
(>def ::default-timezone string?)
(>def ::core-node-data (s/coll-of ::cs.core-node/item))
(>def ::users (s/coll-of ::cs.user/item))

(>def ::seed-data
  (s/keys
   :req-un
   [::default-chains
    ::default-currencies
    ::default-networks
    ::default-rate-sources
    ::default-timezone
    ::core-node-data
    ::users]))
