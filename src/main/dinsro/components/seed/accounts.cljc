(ns dinsro.components.seed.accounts
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::name string?)
(>def ::source string?)
(>def ::initial-value number?)
(>def ::item (s/keys :req-un [::name ::initial-value ::source]))
