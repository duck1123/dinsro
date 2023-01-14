(ns dinsro.components.seed.core
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [dinsro.components.seed.core-nodes :as cs.core-nodes]
   [dinsro.components.seed.currencies :as cs.currencies]
   [dinsro.components.seed.networks :as cs.networks]
   [dinsro.components.seed.rate-sources :as cs.rate-sources]
   [dinsro.components.seed.users :as cs.users]))

(>def ::default-chains (s/coll-of string?))
(>def ::currencies (s/coll-of ::cs.currencies/item))
(>def ::networks cs.networks/item)
(>def ::default-rate-sources (s/coll-of ::cs.rate-sources/item))
(>def ::default-timezone string?)
(>def ::nodes (s/coll-of ::cs.core-nodes/item))
(>def ::category-names (s/coll-of string?))
(>def ::users (s/coll-of ::cs.users/item))
(>def ::addresses (s/coll-of string?))
(>def ::relays (s/coll-of string?))

(>def ::seed-data
  (s/keys
   :req-un
   [::addresses
    ::timezone
    ::category-names
    ::networks
    ::nodes
    ::users
    ::relays
    ::currencies]))
