(ns dinsro.spec.transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.specs :as ds]))

(s/def ::description string?)
(def description ::descriprion)
(def description-spec
  {:db/ident       ::value
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::account (s/keys :req [:db/id]))
(def account ::account)
(def account-spec
  {:db/ident ::account
   :db/valueType :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::currency (s/keys :req [:db/id]))
(def currency ::currency)
(def currency-spec
  {:db/ident ::currency
   :db/valueType :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::date ::ds/date)
(def date ::date)
(def date-spec
  {:db/ident ::date
   :db/valueType :db.type/instant
   :db/cardinality :db.cardinality/one})

(s/def ::value ::ds/valid-double)
(def value ::value)
(def value-spec
  {:db/ident       ::value
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::account-id  ::ds/id)
(s/def ::currency-id ::ds/id)

(s/def ::params (s/keys :req [::account ::currency ::date ::value ::description]))
(def params ::params)
(s/def ::item (s/keys :req [::account ::currency ::date ::value ::description]))
(def item ::item)
(def schema
  [value-spec currency-spec date-spec account-spec description-spec])
