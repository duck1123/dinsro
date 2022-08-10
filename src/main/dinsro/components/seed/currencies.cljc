(ns dinsro.components.seed.currencies
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::name string?)
(>def ::code string?)

(>def ::item (s/keys :req-un [::name ::code]))
