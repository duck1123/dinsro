(ns dinsro.components.seed.transactions
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]
   [dinsro.components.seed.debits :as cs.debits]))

(>def ::date string?)
(>def ::description string?)
(>def ::debits (s/coll-of ::cs.debits/item))

(>def ::item (s/keys :req-un [::date ::description ::debits]))
