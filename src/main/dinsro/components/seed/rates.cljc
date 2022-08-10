(ns dinsro.components.seed.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::item
  (s/keys
   :req-un [;; ::name ::url ::code ::isActive? ::isIdentity ::rates ::path
            ]))
