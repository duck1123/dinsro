(ns dinsro.components.seed.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [dinsro.components.seed.rate-sources :as cs.rate-sources]))

(>def ::name string?)
(>def ::code string?)
(>def ::sources (s/coll-of ::cs.rate-sources/item))

(>def ::item (s/keys :req-un [::name ::code ::sources]))
