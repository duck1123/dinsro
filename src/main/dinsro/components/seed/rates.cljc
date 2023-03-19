(ns dinsro.components.seed.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::date string?)
(>def ::rate number?)

(>def ::item (s/keys :req-un [::date ::rate]))
