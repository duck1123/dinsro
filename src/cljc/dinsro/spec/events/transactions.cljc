(ns dinsro.spec.events.transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec.actions.transactions :as s.a.transactions]
            [dinsro.spec.transactions :as s.transactions]))

(s/def ::items-by-account-event (s/cat :keyword keyword? :id ::s.transactions/account-id))
(def items-by-account-event ::items-by-account-event)

(s/def ::items-by-currency-event (s/cat :keyword keyword? :id ::s.transactions/currency-id))
(def items-by-currency-event ::items-by-currency-event)

(s/def :fetch-transactions-cofx/db (s/keys))
(s/def ::do-fetch-index-cofx (s/keys :req-un [:fetch-transactions-cofx/db]))
(def do-fetch-index-cofx ::do-fetch-index-cofx)

(s/def ::do-fetch-index-event (s/cat :data (s/keys)))
(def do-fetch-index-event ::do-fetch-index-event)

(s/def ::do-fetch-index-response (s/keys))
(def do-fetch-index-response ::do-fetch-index-response)

(s/def ::do-delete-record-success-cofx (s/keys))
(s/def ::do-delete-record-failed-cofx (s/keys))
(s/def ::do-delete-record-cofx (s/keys))
(s/def ::do-delete-record-event (s/cat :item ::s.transactions/item))

(s/def ::do-submit-response (s/keys))
(s/def ::do-submit-success-cofx (s/keys))
(s/def ::do-submit-failed-cofx (s/keys))
(s/def ::do-submit-cofx (s/keys))

(s/def ::do-submit-event (s/cat :data ::s.a.transactions/create-params-valid))
(def do-submit-event ::do-submit-event)
