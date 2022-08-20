(ns dinsro.components.seed.remote-nodes
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::host string?)

(>def ::item (s/keys :req-un [::host]))
