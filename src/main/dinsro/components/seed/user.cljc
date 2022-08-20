(ns dinsro.components.seed.user
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(s/def ::username string?)
(s/def ::password string?)
(s/def ::categories (s/coll-of :dinsro.components.seed.categories/item))
(s/def ::accounts (s/coll-of :dinsro.components.seed.accounts/item))

(>def ::item (s/keys :req-un [::username ::password ::categories ::accounts]))
