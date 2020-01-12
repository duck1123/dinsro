(ns dinsro.spec.accounts
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.spec :as ds]))

(s/def ::name             string?)
(def name ::name)
(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::initial-value ::ds/valid-double)
(def initial-value ::initial-value)
(def initial-value-spec
  {:db/ident       ::initial-value
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::currency-id      ::ds/id)
(def currency-id ::currency-id)
(s/def ::currency         (s/keys :req [:db/id]))
(def currency ::currency)

(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::user-id          ::ds/id)
(def user-id ::user-id)

(s/def ::user             (s/keys :req [:db/id]))
(def user ::user)

(def user-spec
  {:db/ident       ::user
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::params           (s/keys :req [::name ::initial-value ::currency ::user]))
(def params ::params)

(s/def ::item             (s/keys :req [::name ::initial-value ::currency ::user]))
(def item ::item)

(def item-spec
  {:db/ident ::item
   :db.entity/attrs [::name ::initial-value ::currency ::user]})

(def schema
  [name-spec initial-value-spec currency-spec user-spec])
