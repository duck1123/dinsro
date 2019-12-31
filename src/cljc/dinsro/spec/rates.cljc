(ns dinsro.spec.rates
  (:require [clojure.spec.alpha :as s]))

(s/def ::rate (s/and number? pos?))
(def rate-spec
  {:db/ident       ::rate
   :db/valueType   :db.type/double
   :db/cardinality :db.cardinality/one})

(s/def ::currency (s/keys :req []))
(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::date inst?)
(s/def ::date-inst inst?)

(def date-spec
  {:db/ident ::date
   :db/valueType :db.type/instant
   :db/cardinality :db.cardinality/one})

(s/def ::params (s/keys :req [::rate ::currency ::date]))
(s/def ::item (s/keys :req [::rate ::currency ::date]))
(def item-spec
  {:db/ident ::item
   :db.entity/attrs [::rate ::currency ::date]})

(def schema
  [rate-spec currency-spec date-spec #_item-spec])
