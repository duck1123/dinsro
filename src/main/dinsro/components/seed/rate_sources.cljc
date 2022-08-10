(ns dinsro.components.seed.rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [dinsro.components.seed.rates :as cs.rates]))

(>def ::name string?)
(>def ::url string?)
(>def ::code string?)
(>def ::isActive boolean?)
(>def ::isIdentity boolean?)
(>def ::rates (s/coll-of ::cs.rates/item))
(>def ::path string?)

(>def ::item
  (s/keys
   :req-un [::name ::url ::code ::isActive ::isIdentity ::rates ::path]))
