(ns dinsro.components.seed.categories
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::name string?)

(>def ::item (s/keys :req-un [::name]))
