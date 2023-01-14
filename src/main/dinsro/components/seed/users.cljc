(ns dinsro.components.seed.users
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def]]))

(s/def ::username string?)
(s/def ::password string?)
(>def ::pubkey string?)
(>def ::role keyword?)
(s/def ::accounts (s/coll-of :dinsro.components.seed.accounts/item))
(s/def ::categories (s/coll-of :dinsro.components.seed.categories/item))
(>def ::transactions (s/coll-of :dinsro.components.seed.transactions/item))
(>def ::ln-nodes (s/coll-of :dinsro.components.seed.ln-nodes/item))

(>def ::item
  (s/keys
   :req-un
   [::username
    ::password
    ::pubkey
    ::role
    ::accounts
    ::categories
    ::transactions
    ::ln-nodes]))
