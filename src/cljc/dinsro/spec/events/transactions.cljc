(ns dinsro.spec.events.transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]))

(s/def ::items-by-account-event (s/cat :keyword keyword? :id ::s.transactions/account-id))
(def items-by-account-event ::items-by-account-event)

(s/def ::items-by-currency-event (s/cat :keyword keyword? :id ::s.transactions/currency-id))
(def items-by-currency-event ::items-by-currency-event)

(s/def ::do-delete-record-success-cofx (s/keys))
(s/def ::do-delete-record-failed-cofx (s/keys))
(s/def ::do-delete-record-cofx (s/keys))
(s/def ::do-delete-record-event (s/cat :item ::s.transactions/item))
