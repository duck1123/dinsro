(ns dinsro.components.seed.wallets
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(>def ::label string?)
(>def ::blockheight number?)
(>def ::name string?)
(>def ::descriptor string?)
(>def ::seed (s/coll-of string?))
(>def ::node string?)
(>def ::path string?)

(>def ::item (s/keys :req-un [::label ::blockheight ::name ::descriptor
                              ::node ::path]
                     :opt-un [::seed]))
