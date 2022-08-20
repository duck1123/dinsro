(ns dinsro.components.seed.networks
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::item (s/map-of string? (s/coll-of string?)))
(def item ::item)
