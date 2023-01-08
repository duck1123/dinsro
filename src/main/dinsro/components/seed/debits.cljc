(ns dinsro.components.seed.debits
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::value number?)
(>def ::account string?)

(>def ::item (s/keys :req-un [::value ::account]))
