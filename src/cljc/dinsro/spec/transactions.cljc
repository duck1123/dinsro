(ns dinsro.spec.transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.spec :as ds]))

(s/def ::description string?)
(def description ::descriprion)
(def description-spec
  {:db/ident       ::description
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::account (s/keys :req [:db/id]))
(def account ::account)
(def account-spec
  {:db/ident ::account
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

(s/def ::params (s/keys :req [::account ::date ::description ::value]))
(def params ::params)

(s/def ::item (s/keys :req [:db/id ::account ::date ::description ::value]))
(def item ::item)

(def schema
  [account-spec date-spec description-spec value-spec])
