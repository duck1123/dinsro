(ns dinsro.model.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs]))

(s/def ::id        string?)
(def id            ::id)
(def id-spec
  {:db/ident       id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::name string?)
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

(s/def ::currency
  (s/keys :opt [:db/id
                ::m.currencies/id]))
(def currency ::currency)

(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::required-params (s/keys :req [::name ::url]))
(def required-params ::required-params)

(s/def ::params (s/keys :req [::name ::url ::currency]))
(def params ::params)

(s/def ::item (s/keys :req [::id ::name ::url ::currency]))
(def item ::item)

(s/def ::items (s/coll-of ::item))
(def items ::items)

(s/def ::ident (s/tuple keyword? ::id))

(>defn ident
  [id]
  [::id => ::ident]
  [::id id])

(>defn ident-item
  [{::keys [id]}]
  [::item => ::ident]
  (ident id))

(def schema
  [currency-spec
   id-spec
   name-spec
   url-spec])
