(ns dinsro.spec.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::name             string?)
(def name ::name)

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::url string?)
(def url ::url)

(def url-spec
  {:db/ident       ::url
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::currency-id      :db/id)
(def currency-id ::currency-id)

(s/def ::currency         (s/keys :req [:db/id]))
(def currency ::currency)

(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::params (s/keys :req [::name ::url ::currency]))
(def params ::params)

(s/def ::item (s/keys :req [::name ::url ::currency]))
(def item ::item)

(def schema
  [name-spec url-spec currency-spec])
