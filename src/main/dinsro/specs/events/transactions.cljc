(ns dinsro.specs.events.transactions
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.specs.actions.transactions :as s.a.transactions]))

(s/def ::items-by-account-event (s/cat :keyword keyword? :id ::m.transactions/account-id))
(def items-by-account-event ::items-by-account-event)

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
(s/def ::do-delete-record-event (s/cat :item ::m.transactions/item))

(s/def ::do-submit-response (s/keys))
(s/def ::do-submit-success-cofx (s/keys))
(s/def ::do-submit-failed-cofx (s/keys))
(s/def ::do-submit-cofx (s/keys))

(s/def ::do-submit-event (s/cat :data ::s.a.transactions/create-params-valid))
(def do-submit-event ::do-submit-event)
