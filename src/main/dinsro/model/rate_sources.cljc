(ns dinsro.model.rate-sources
  (:refer-clojure :exclude [name])
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.specs]))

(s/def ::id        string?)
(def id-spec
  {:db/ident       ::id
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one
   :db/unique      :db.unique/identity})

(s/def ::name string?)

(def name-spec
  {:db/ident       ::name
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::url string?)

(def url-spec
  {:db/ident       ::url
   :db/valueType   :db.type/string
   :db/cardinality :db.cardinality/one})

(s/def ::currency
  (s/keys :opt [:db/id
                ::m.currencies/id]))

(def currency-spec
  {:db/ident       ::currency
   :db/valueType   :db.type/ref
   :db/cardinality :db.cardinality/one})

(s/def ::required-params (s/keys :req [::name ::url]))

(s/def ::params (s/keys :req [::name ::url ::currency]))

(s/def ::item (s/keys :req [::id ::name ::url ::currency]))

(s/def ::items (s/coll-of ::item))

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
